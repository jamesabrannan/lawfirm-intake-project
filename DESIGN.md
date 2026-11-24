<!--
File: DESIGN.md
Author: James Brannan
Date: 2026-11-23
Description: Architecture, data models, and execution design of the three law firm demonstrations.
Copyright (c) 2026 James Brannan.
Licensed under the MIT License. See LICENSE in the repository root for the full terms.
SPDX-License-Identifier: MIT
-->

# Law Firm Workflow Demonstrations — Design

This document describes the implemented demonstrations as inspected on November 23, 2026. The three major sections follow intake preparation, preliminary research, and memorandum review. Each project runs independently with its own fixtures; their relationship is a business workflow, not an implemented automated integration.

Diagrams use Mermaid, which GitHub renders in Markdown. Class and sequence diagrams use UML notation; flowcharts describe workflow routing, and ER diagrams describe relational structure. Class diagrams show selected responsibilities and dependencies rather than every accessor or model field.

All case material is fictional. These prototypes demonstrate software engineering approaches, not validated legal procedures. The author does not have professional legal domain expertise; workflow assumptions and legal outputs require validation with attorneys and law firm staff.

## 1. n8n: Intake preparation

### 1.1 Purpose and boundaries

The workflow prepares intake material for staff review. It combines CSV records, checks for missing documents, extracts PDF text, creates an intake brief, and drafts missing-document and handoff messages. Email artifacts are saved to disk rather than transmitted. A separate AI review gates newly generated brief PDFs; an AI approval is not attorney approval.

The executable definition is [My workflow.json](project/n8n_work/My%20workflow.json). Deployment is defined by [compose.yml](project/n8n_work/compose.yml).

### 1.2 Deployment and storage

```mermaid
flowchart LR
    Staff[Staff browser] -->|localhost:5678| N8N[n8n Docker container]
    N8N -->|Model requests| AI[OpenAI API]
    State[storage/n8n] <-->|/home/node/.n8n| N8N
    Files[storage/files] <-->|/files/output| N8N
    Resources[../resources additional mount] -->|/files/resources| N8N
    Env[Local openai-key.txt] -->|Container environment| N8N
```

The port is published on `127.0.0.1`. Bind mounts preserve n8n configuration and workflow state outside the container. Deleting the container does not itself delete those host folders. They must still be protected and backed up, including configuration needed to decrypt saved credentials.

The current workflow reads CSV and PDF fixtures from `/files/output/resource/`, corresponding to `storage/files/resource/`, and writes artifacts to `/files/output/outbox/`. The additional `/files/resources` mount is not the source path used by those workflow nodes. Ignored local storage is not supplied by a Git clone.

### 1.3 Workflow routing

The screenshot below shows the workflow in the n8n editor. Open the image at full size to inspect individual nodes and connections.

![n8n intake workflow showing CSV ingestion, missing-document requests, brief generation and validation, and paralegal handoff](n8n.png)

[View the full-size workflow screenshot](n8n.png).

The Mermaid diagram below summarizes the exported node connections. Node groups keep the routing readable alongside the editor screenshot.

```mermaid
flowchart TD
    Start[Manual Execute workflow] --> Read[Read CSV files and extract rows]
    Read --> Join[Combine Records to JSON]
    Join --> Loop[Loop Over Items]
    Loop --> Record[Extract Record and calculate missing documents]
    Record --> Missing{Document request needed?}
    Missing -->|Yes| Email[Create Email]
    Email --> ReviewEmail[Message a model and parse review]
    ReviewEmail --> EmailOK{Review passes?}
    EmailOK -->|Yes| SaveEmail[Save missing-document email text]
    EmailOK -->|No| DocDone[Document Path Complete]
    SaveEmail --> DocDone
    Missing -->|No| DocDone
    Record --> Exists{Brief already created?}
    Exists -->|No| PDFs[Read source PDFs and extract text]
    PDFs --> Evidence[Assemble Brief Evidence]
    Evidence --> Draft[Create Intake Brief and parse draft]
    Draft --> Review[Validate Intake Brief and parse review]
    Review --> Approved{Brief Approved?}
    Approved -->|Yes| SavePDF[Render and save intake brief PDF]
    SavePDF --> Ready[New Brief Ready for Handoff]
    Ready --> Sent{Brief already sent?}
    Exists -->|Yes| Sent
    Sent -->|No| Handoff[Prepare and save paralegal email text]
    Sent -->|Yes| Merge[Join Completed Paths]
    Handoff --> Merge
    Approved -->|No| Reject[Brief Review Rejected]
    Reject --> Merge
    DocDone --> Merge
    Merge --> Loop
```

Both the document-request route and the brief route originate from the extracted intake record. The merge node rejoins routes before advancing the loop; the picture does not promise concurrent execution of external requests.

### 1.4 Logical input model

The CSV files are joined in JavaScript using `contact_id`, not database foreign keys. Duplicate or missing join keys and missing related records cause errors. Pipe-delimited document fields are converted into arrays, and literal `true`/`false` strings are converted to booleans.

```mermaid
classDiagram
    class IntakeWorkItem {
        contact_id
        client
        intake
        clerk_assignment
        paralegal_assignment
        missing_documents
        document_request_needed
    }
    class ClientRecord {
        contact_id
        first_name
        last_name
    }
    class IntakeRecord {
        intake_id
        contact_id
        document_filenames
        expected_document_types
    }
    class ClerkAssignment {
        contact_id
        intake_brief_created
        missing_documents_request_sent_to_client
    }
    class ParalegalAssignment {
        contact_id
        research_memorandum_status
    }
    IntakeWorkItem *-- ClientRecord
    IntakeWorkItem *-- IntakeRecord
    IntakeWorkItem *-- ClerkAssignment
    IntakeWorkItem *-- ParalegalAssignment
```

These are conceptual record shapes implemented as JavaScript objects, not declared application classes. The missing-document check compares expected types with filenames after removing the intake prefix and `.pdf` suffix. Correct naming is therefore part of the fixture contract; a filename match does not prove document completeness or authenticity.

### 1.5 Brief generation sequence

```mermaid
sequenceDiagram
    actor Staff
    participant Flow as n8n workflow
    participant Files as Mounted files
    participant AI as OpenAI
    Staff->>Flow: Execute workflow
    Flow->>Files: Read CSV records
    Files-->>Flow: Intake work items
    loop Each intake needing a new brief
        Flow->>Files: Read listed PDFs
        Files-->>Flow: PDF bytes
        Flow->>Flow: Extract text and assemble evidence
        Flow->>AI: Request intake brief
        AI-->>Flow: Draft response
        Flow->>Flow: Parse draft
        Flow->>AI: Request brief validation
        AI-->>Flow: Review response
        alt Review approved
            Flow->>Flow: Render brief PDF
            Flow->>Files: Save PDF and applicable handoff text
        else Review rejected
            Flow->>Flow: Record rejection route
        end
    end
```

### 1.6 Outputs, failure handling, and tradeoffs

- Output artifacts include intake brief PDFs, missing-document email drafts, and paralegal handoff drafts in `storage/files/outbox/`.
- Status flags control skipping and handoff behavior. The workflow does not implement durable updates to the input CSV files or an external CRM, so reruns should not be assumed idempotent.
- The PDF renderer is local JavaScript and normalizes text to a limited character set. It avoids another rendering service but is not a general document-layout engine.
- Missing related records, unreadable files, model failures, or parse failures can interrupt execution. The explicit rejection route handles a rejected brief review; it is not a general exception recovery system.
- Environment access is enabled for the workflow's API-key expression. Credentials, n8n storage, and execution data belong outside source control. Production use would require access controls, audit policy, durable work state, and domain-approved review criteria.

## 2. Java / Spring Boot: Research memorandum preparation

### 2.1 Architecture and API contract

The application uses Spring MVC, Spring Security, Spring JDBC, Spring AI, SQLite, and PDFBox. A synchronous `POST /api/memorandums/prepare` starts work for the authenticated staff member. HTTP Basic authentication and the `MEMORANDUM_PREPARER` authority guard the endpoint. No body supplies a staff identity; work selection derives identity from Spring Security's context.

The response is a completion summary rather than a PDF or background-job receipt. Files are stored locally under the configured `memorandum.output-directory`, defaulting to `output/memorandums`.

```mermaid
flowchart LR
    Client[HTTP client] --> Security[Spring Security]
    Security --> Controller[MemorandumController]
    Controller --> Processing[Processing and preparation services]
    Processing <--> DB[(In-memory SQLite)]
    Docs[Classpath PDF fixtures] --> Processing
    Prompts[Classpath prompt templates] --> Processing
    Processing <--> AI[OpenAI through Spring AI]
    Processing <--> CL[CourtListener API]
    Processing --> PDF[Local memorandum PDFs]
    Processing --> Logs[Console and rolling file logs]
```

### 2.2 Service responsibilities and dependencies

```mermaid
classDiagram
    class MemorandumController {
        +prepare() MemorandumPreparationResponse
    }
    class MemorandumProcessingService {
        +prepareForCurrentStaff() MemorandumPreparationResponse
    }
    class MemorandumWorkService {
        +findRequestedForCurrentStaff() List
    }
    class CaseDetailsService
    class CaseDetailsRepository
    class MemorandumPreparationService {
        +prepare(CaseDetails) void
    }
    class CaseInputService {
        +assemble(CaseDetails) CaseInput
    }
    class PdfDocumentService
    class ResearchPlanningService {
        +createPlan(CaseInput) ResearchPlan
    }
    class CourtListenerResearchService
    class CourtListenerSearchService
    class ResearchEvidenceAssessmentService
    class MemorandumDraftingService
    class MemorandumPdfService
    class MemorandumOutputService
    class MemorandumCompletionRepository
    MemorandumController --> MemorandumProcessingService
    MemorandumProcessingService --> MemorandumWorkService
    MemorandumWorkService --> CaseDetailsService
    CaseDetailsService --> CaseDetailsRepository
    MemorandumProcessingService --> MemorandumPreparationService
    MemorandumPreparationService --> CaseInputService
    CaseInputService --> PdfDocumentService
    MemorandumPreparationService --> ResearchPlanningService
    MemorandumPreparationService --> CourtListenerResearchService
    CourtListenerResearchService --> CourtListenerSearchService
    MemorandumPreparationService --> ResearchEvidenceAssessmentService
    MemorandumPreparationService --> MemorandumDraftingService
    MemorandumPreparationService --> MemorandumPdfService
    MemorandumPreparationService --> MemorandumOutputService
    MemorandumPreparationService --> MemorandumCompletionRepository
```

Work selection is separated from preparation. The preparation service accepts selected `CaseDetails` and coordinates the stages; it does not establish the caller's identity or repeat authorization rules. Prompt templates live in `src/main/resources/prompts/`.

### 2.3 Relational model

The schema below is shared in structure with the notebook fixtures, but the databases are separate. Attributes shown are selected fields from the actual SQL schema. Sensitive fields exist in the fictional database but are not necessary in this diagram.

```mermaid
erDiagram
    users ||--o{ active_intakes : assigned_paralegal
    users ||--o{ active_intakes : assigned_lawclerk
    clients ||--o{ active_intakes : has
    active_intakes ||--o| research_memorandums : has
    users {
        TEXT staff_id PK
        TEXT staff_name
        TEXT email UK
        TEXT role
    }
    clients {
        TEXT contact_id PK
        TEXT first_name
        TEXT last_name
        TEXT email
    }
    active_intakes {
        TEXT intake_id PK
        TEXT contact_id FK
        TEXT assigned_paralegal_id FK
        TEXT assigned_lawclerk_id FK
        TEXT status
        TEXT summary
        TEXT document_filenames
        TEXT expected_document_types
    }
    research_memorandums {
        INTEGER id PK
        TEXT intake_id FK, UK
        TEXT research_memorandum_status
        TEXT filename
    }
```

Each intake requires a client and two staff assignments. A unique `intake_id` in `research_memorandums` permits at most one memorandum row per intake. Status is constrained to `requested` or `completed` when non-null. Document lists remain delimited text fields rather than normalized document tables.

SQLite is seeded from `schema.sql` and `data.sql` at startup. A single pooled physical connection preserves the in-memory database for the application lifetime. Restarting resets database state but does not remove output PDFs.

### 2.4 End-to-end sequence

```mermaid
sequenceDiagram
    actor Staff
    participant API as Security and controller
    participant Work as Work selection
    participant Prep as Preparation service
    participant DB as SQLite
    participant AI as OpenAI
    participant CL as CourtListener
    participant Files as PDF output
    Staff->>API: POST /api/memorandums/prepare with Basic auth
    API->>DB: Authenticate and load authority
    API->>Work: Find requested work for current staff
    Work->>DB: Query assigned requested memorandums
    DB-->>Work: CaseDetails list
    Work-->>API: Selected work through processing service
    loop Each selected memorandum, sequentially
        API->>Prep: prepare(details) through processing service
        Prep->>Prep: Assemble intake and page-preserving PDF text
        Prep->>AI: Research planning prompt and structured output format
        AI-->>Prep: ResearchPlan JSON
        Prep->>Prep: Convert and validate plan
        Prep->>CL: Paced opinion searches for planned topics
        CL-->>Prep: Preliminary case leads
        Prep->>AI: Assess retrieved evidence against questions
        AI-->>Prep: Evidence assessment JSON
        Prep->>Prep: Convert and validate assessment
        Prep->>AI: Draft from input, plan, leads, and assessment
        AI-->>Prep: Memorandum draft JSON
        Prep->>Prep: Validate draft and render PDF
        Prep->>Files: Save uniquely named PDF
        Prep->>DB: Conditional requested-to-completed update
        alt Completion update fails
            Prep->>Files: Attempt removal of newly saved PDF
            Prep-->>API: Propagate failure
        else Completion succeeds
            Prep-->>API: Preparation complete
        end
    end
    API-->>Staff: Completion summary after successful processing
```

The controller delegates the loop to `MemorandumProcessingService`; the sequence groups those entry-point interactions to keep external processing visible. On failure, the successful-summary path is not taken. Earlier completed memorandums are not rolled back if a later one fails.

### 2.5 Evidence contracts and validation

`CaseInput` combines case details, extracted documents, and missing filenames. Documents carry source identifiers and page records. `ResearchPlan` contains topics, questions, queries, and source-attributed facts. CourtListener result models retain retrieved lead information; evidence-assessment models relate research questions to those results. `MemorandumDraft` supplies structured content to the PDF renderer.

```mermaid
classDiagram
    class CaseInput
    class CaseDetails
    class DocumentInput
    class DocumentPageInput
    class ResearchPlan
    class ResearchTopic
    class ResearchFact
    class ResearchEvidenceAssessment
    class ResearchQuestionAssessment
    class MemorandumDraft
    CaseInput --> CaseDetails
    CaseInput "1" *-- "0..*" DocumentInput
    DocumentInput "1" *-- "0..*" DocumentPageInput
    ResearchPlan "1" *-- "1..*" ResearchTopic
    ResearchTopic "1" *-- "0..*" ResearchFact
    ResearchEvidenceAssessment "1" *-- "0..*" ResearchQuestionAssessment
    ResearchPlan ..> CaseInput : grounded in sources
    ResearchEvidenceAssessment ..> ResearchPlan : evaluates questions
    MemorandumDraft ..> ResearchEvidenceAssessment : informed by
```

Unknown source IDs in planned facts fail validation. Missing or invalid page references warn and continue for the demo; a valid page number does not establish that the page supports the statement. CourtListener leads and AI assessments do not independently establish a holding, citation accuracy, or legal applicability.

The current configuration executes at most one planned query per topic, retrieves at most five results per search, and spaces requests by 13 seconds in this JVM. These are demo limits, not guarantees about account-wide rate-limit compliance. PDF extraction reads embedded text and does not perform OCR.

### 2.6 Completion, observability, and deployment tradeoffs

The output PDF is saved before the database update. Completion uses memorandum ID, intake ID, and `requested` status as conditions. If that update fails, preparation attempts to remove the newly written file and propagates the failure, preserving cleanup failures as suppressed exceptions. Filesystem and database writes are not one atomic transaction; process crashes can still leave orphan files.

SLF4J writes to the console and a rolling log file. OpenAI prompt text and raw response JSON are logged separately for fictional-demo inspection. Shared external HTTP logging records bounded, sanitized transport metadata without bodies or credentials. Production handling would need an approved sensitive-data policy.

Synchronous processing keeps the demonstration easy to follow. A deployed service should use asynchronous jobs, persistent status, controlled retries, concurrency limits, and result retrieval. Those capabilities are proposals, not present endpoints. A persistent database and durable document storage would also be needed. The demo's plain-text seeded passwords and disabled CSRF protection are not suitable production defaults.

Tests cover authentication/authorization, repositories, case input, AI stages with mocked responses, research processing, and file/completion behavior. This design-document task did not run tests or issue live API calls.

## 3. Python / Jupyter: Memorandum review

### 3.1 Purpose and execution model

[legal-demo.ipynb](project/jupyter-notebook/summarize-docs-data/legal-demo.ipynb) is an interactive review workflow for an individual staff member. Cells run in order and share Python variables in the kernel. The implementation uses functions from libraries and dictionaries/lists; it does not define an application class hierarchy.

The notebook compares each memorandum with its own intake record and supporting PDFs. It asks for discrepancies, missing information, unsupported conclusions, and recommended revisions, then displays and exports the returned reviews. It does not search CourtListener or independently verify legal authorities.

### 3.2 Data flow and relational inputs

```mermaid
flowchart LR
    Env[Local .env] --> Kernel[Jupyter Python kernel]
    DB[(resources/data.db read-only)] -->|SQL joins| Kernel
    Docs[resources/docs PDFs] -->|pypdf text extraction| Kernel
    Kernel --> Payload[Evaluations JSON grouped by memorandum]
    Payload --> AI[OpenAI Responses API]
    AI --> Reviews[Parsed reviews JSON]
    Reviews --> Display[Markdown notebook display]
    Reviews --> PDF[fpdf2 review PDFs in output]
```

The local database uses the four-table ER model in Section 2.3. The query starts from `research_memorandums` and uses left joins to intake, client, and two staff aliases, preserving memorandum rows when a join does not match. It excludes passwords and client SSNs from the selected fields. The database connection uses SQLite read-only URI mode and closes in `finally`.

This is a separate fixture database, not a connection to the Java process's in-memory SQLite database. Memorandum filenames in the notebook database must match PDFs supplied in the notebook's `resources/docs` folder. Any transfer of Java outputs and corresponding fixture updates is manual.

### 3.3 Payload and response contracts

The following UML diagram describes JSON/data shapes rather than Python classes.

```mermaid
classDiagram
    class EvaluationPayload {
        evaluations
    }
    class Evaluation {
        memorandum_id
        intake_id
        intake
        memorandum
        supporting_documents
    }
    class MemorandumText {
        status
        filename
        text
    }
    class SupportingDocument {
        filename
        text
    }
    class EvaluationResults {
        reviews
    }
    class Review {
        memorandum_id
        intake_id
        memorandum_filename
        review_memo
    }
    EvaluationPayload "1" *-- "0..*" Evaluation
    Evaluation "1" *-- "1" MemorandumText
    Evaluation "1" *-- "0..*" SupportingDocument
    EvaluationResults "1" *-- "0..*" Review
    Review ..> Evaluation : identifiers link result to input
```

Supporting-document filenames come from each intake's pipe-delimited list. Each evaluation includes only that intake's listed evidence, even though the extraction cell reads all PDFs in the document directory. Missing dictionary entries raise `KeyError`. A convenience cell also assumes the first two memorandum filenames have matching extracted text.

### 3.4 Review sequence

```mermaid
sequenceDiagram
    actor Staff
    participant NB as Notebook kernel
    participant DB as Read-only SQLite
    participant Docs as Local PDFs
    participant AI as OpenAI Responses API
    participant Out as Display and output PDFs
    Staff->>NB: Run cells in order
    NB->>NB: Load .env and define SQL
    NB->>DB: Query memorandum, intake, client, and staff
    DB-->>NB: Rows
    NB->>DB: Close connection
    NB->>Docs: Extract embedded PDF text
    Docs-->>NB: Text keyed by filename
    NB->>NB: Build independent evaluation entries
    NB->>AI: One combined request with strict JSON schema
    AI-->>NB: Response with reviews array
    NB->>NB: Check completion and parse JSON
    loop Each returned review
        NB->>Out: Display Markdown
        NB->>Out: Export memorandum-ID review PDF
    end
    Out-->>Staff: Review text and local PDF files
```

### 3.5 Review instructions and validation boundaries

The prompt requires independent evaluation of each entry and distinguishes documented evidence, client reports, allegations, assumptions, and conclusions. Source text is treated as material to assess rather than instructions. Findings should identify source filenames or intake fields, preserve conflicts, and avoid invented facts, page numbers, and authorities.

The request uses `gpt-4.1`, a strict JSON schema, a 12,000-token output limit, and `store=False`. The latter is a request storage setting, not a blanket claim about provider retention. The notebook checks response completion and parses JSON. The schema constrains output shape, but the current code does not establish that every input has exactly one correctly matched review or that the review is accurate.

### 3.6 Output and operating limitations

The notebook renders review text as Markdown for display. The PDF exporter writes the same text literally using `fpdf2`, so Markdown markers can remain visible. Files are named `memorandum-<id>-review.pdf`; subsequent runs overwrite matching files.

Paths currently assume `D:\lawfirm-intake-project`, and PDF export uses Windows Arial at `C:\Windows\Fonts\arial.ttf`. Portability requires path and font changes. Dependencies include `python-dotenv`, `pypdf`, `openai`, and `fpdf2`, alongside Jupyter.

The combined API request sends all assembled evaluation entries together. Large fixture sets can exceed practical context or output limits; batching, explicit input/output ID reconciliation, durable review status, and stronger export formatting would be future improvements. The notebook is an interactive prototype, without service authentication, background jobs, or an automated integration with the other demos.

An AI-generated review is another aid for human inspection, not independent legal verification. The final review responsibility remains with qualified staff and the supervising attorney.
