<!--
File: README.md
Author: James Brannan
Date: 2026-11-23
Description: Overview and setup notes for the three law firm workflow demonstrations.
Copyright (c) 2026 James Brannan.
Licensed under the MIT License. See LICENSE in the repository root
for the license text. SPDX-License-Identifier: MIT
-->

# Law Firm Workflow Demonstrations

This project includes three demonstrations showing how AI automation can assist with intake preparation, preliminary legal research, and reviewing research memoranda. The examples use fictional clients, intake records, and supporting documents. This project was created to demonstrate relevant skills for an AI automation engineer position. The demos include an n8n workflow, a Spring AI microservice with a single REST endpoint, and an interactive Python application in a Jupyter notebook.

* Start with the [brief overview](OVERVIEW.md) for a plain-language explanation of the demonstrations and links to the videos.
* See the [design document](DESIGN.md) for architecture, UML class and sequence diagrams, workflow routing, and the relational data model for all three demonstrations.

> [!NOTE]
> *My legal domain knowledge is limited. These examples are intended to demonstrate AI automation engineering tasks; the legal scenarios and workflow assumptions do not necessarily illustrate actual work practices of law firm staff.*

| Demo | Purpose | Implementation |
| --- | --- | --- |
| [Intake preparation](project/n8n_work) | Identify missing documents, prepare intake briefs, and draft staff handoffs | n8n, JavaScript, OpenAI, CSV and PDF files |
| [Research memorandum preparation](project/spring-boot-work/legal-research) | Select assigned work, assemble case evidence, search for authorities, and generate preliminary memorandums | Java 17, Spring Boot, Spring AI, Spring Security, SQLite, PDFBox, CourtListener, OpenAI |
| [Memorandum review](project/jupyter-notebook/summarize-docs-data/legal-demo.ipynb) | Compare memorandums with intake information and supporting documents | Python, Jupyter, SQLite, OpenAI, pypdf, fpdf2 |

Together, these illustrate successive staff activities. They are separate demonstrations with their own fixtures and execution steps; they do not automatically transfer records or outputs between projects.

## Watch the demonstrations

Brief screen recordings show each demo executing and producing its results. No installation is needed to view them.

> [!NOTE]
> If a video does not play on GitHub, open its link and select **Download raw file** (the downward-arrow icon in the upper-right corner of the file viewer, above the preview area). Open the downloaded MP4 in your video player.

- [n8n intake workflow](n8n-run.mp4)
- [Java research memorandum workflow](spring-run.mp4)
- [Jupyter notebook memorandum review](jupyter-notebook-run.mp4)

## 1. Intake preparation with n8n

The manually triggered n8n workflow prepares intake briefs, missing-document requests, and paralegal handoff messages from fictional CSV records and supporting PDFs.

The intake preparation workflow:

1. Loads client records, active intakes, and staff assignments from CSV files.
2. Combines related records and checks the expected document checklist.
3. Routes work according to assignment and completion status flags.
4. Uses OpenAI to draft missing-document requests and submits them to a separate AI review step before saving approved requests.
5. Extracts supporting PDF text and asks OpenAI to draft an intake brief.
6. Submits the draft to a separate AI review step.
7. Renders approved briefs as PDFs and saves missing-document requests and paralegal handoff messages as text files for inspection.

**Main files:** [workflow export](project/n8n_work/My%20workflow.json) and [Docker Compose configuration](project/n8n_work/compose.yml).

### Running locally

1. Install Docker Desktop with Docker Compose and start Docker Desktop.

2. In `project/n8n_work`, create `openai-key.txt` containing:

   ```dotenv
   OPENAI_API_KEY=your-openai-api-key
   ```

   The supplied `openai-key.example.txt` is currently empty. Docker Compose loads `openai-key.txt` as an environment file.

3. Confirm that `storage/files/resource/` contains the demo fixtures:

   - `clients.csv`
   - `active_intakes.csv`
   - `lawclerk_assignments.csv`
   - `paralegal_assignments.csv`
   - The supporting PDFs referenced by the intake records

   Create `storage/files/outbox/` if it does not exist. Inside the container, these directories are available as `/files/output/resource/` and `/files/output/outbox/`.

4. From `project/n8n_work`, run:

   ```powershell
   docker compose up -d n8n
   ```

5. Open http://localhost:5678, complete the initial n8n account setup, and import `My workflow.json`.

6. Create an OpenAI credential in n8n using your API key and select it in all four OpenAI nodes:

   - `Create Email`
   - `Message a model`
   - `Create Intake Brief`
   - `Validate Intake Brief`

7. Confirm that the configured `chat-latest` model is available to your API project, or select an accessible model in all four OpenAI nodes.

8. Click **Execute workflow**. Inspect the execution results, including the AI review decisions. Generated intake briefs and email drafts are saved under `storage/files/outbox/`. Rejected drafts are not saved as approved outputs.

9. Review the generated PDFs and text files. Missing-document requests and paralegal handoff messages are saved for inspection; the workflow does not send emails or persist changes to the input CSV assignment flags.

The Compose configuration preserves n8n’s database, credentials, and configuration under `storage/n8n/`. The workflow reads assignment flags from the CSV fixtures; it does not update those files or an external CRM. Repeated executions may overwrite output files with matching names.

## 2. Memorandum preparation with Java Spring AI

The Spring Boot application uses Spring AI and exposes a synchronous `POST /api/memorandums/prepare` endpoint. Spring Security authenticates the staff member using HTTP Basic and requires the `MEMORANDUM_PREPARER` authority. The application selects work based on the authenticated staff member’s identity and assigned records, then uses Spring AI with OpenAI to perform the AI processing.

The preparation workflow:

1. Selects requested memorandums assigned to the authenticated staff member and loads related case information from SQLite.
2. Extracts text from intake briefs and supporting PDFs, preserves page numbers, and combines it with the case information.
3. Uses Spring AI with OpenAI to create a structured research plan containing research topics, questions, and search queries.
4. Searches the CourtListener API using up to one planned query per topic, retrieving case metadata, source links, and opinion snippets as preliminary research leads.
5. Uses Spring AI with OpenAI to assess the search results against the research questions and identify research gaps.
6. Uses a separate OpenAI call through Spring AI to draft a preliminary memorandum from the case information, research plan, search results, and assessment.
7. Renders and saves the memorandum as a PDF, then records its filename and changes its SQLite status from `requested` to `completed`, provided the record still matches the intake and remains requested.
8. Returns a JSON summary after all selected memorandums finish processing.

The endpoint waits for processing to finish and returns a JSON summary. It does not return the PDF itself. Generated memorandums are saved under `output/memorandums/` in the application working directory.

> [!NOTE]
> The SQLite database is created in memory when the application starts and is discarded when the application stops.

### Running locally

1. Install JDK 17 or later and Maven. Confirm that `java` and `mvn` are available in your terminal.

2. Set `OPENAI_API_KEY` and `COURTLISTENER_API_KEY` in the environment inherited by the application. For PowerShell:

   ```powershell
   $env:OPENAI_API_KEY = "your-openai-api-key"
   $env:COURTLISTENER_API_KEY = "your-courtlistener-api-key"
   ```

   Example key files are available in `project/spring-boot-work`, but the application does not load those files automatically. The configured OpenAI model is `gpt-6.2-sol`.

3. Confirm that `src/main/resources/docs` contains the fictional intake briefs and supporting PDFs. The application reads these files from the classpath, using the supporting filenames in `data.sql` and the intake-brief naming convention `<intake-id>-intake-brief.pdf`.

4. From `project/spring-boot-work/legal-research`, run:

   ```powershell
   mvn spring-boot:run
   ```

   Startup creates and seeds an in-memory SQLite database using `src/main/resources/schema.sql` and `data.sql`. No separate database installation or database file is required.

5. Use Postman or another HTTP client to send:

   ```http
   POST http://localhost:8080/api/memorandums/prepare
   ```

   Select **Basic Auth** and use the seeded demo credentials:

   - **Username:** `riley.morgan@example.com`
   - **Password:** `password`

   No request body is required. Riley has the `MEMORANDUM_PREPARER` authority and is assigned both seeded intakes. The other seeded memorandum preparer, Jordan, has no assigned work in the current fixtures.

6. Allow the request to finish. Processing is synchronous, includes multiple OpenAI calls, and spaces CourtListener searches at least 13 seconds apart. Increase your HTTP client’s request timeout if necessary.

7. Inspect the JSON completion summary and the generated PDFs in `output/memorandums/`. The endpoint returns a summary rather than the PDFs themselves. Logs appear in the console and in `logs/researchmemorandum.log`.

8. Sending the request again after successful completion returns `no_work` for those records. Restarting the application recreates the seeded database and resets completion state; previously generated PDFs remain on disk.

The source includes tests for security, work selection, PDF extraction, structured AI stages, research processing, and output completion. Run them locally with `mvn test`.

> [!NOTE]
> All data and scenarios are fictional and simplified for demonstration purposes. Security, error handling, and retry behavior are also simplified. The application processes requests synchronously to make the workflow easy to follow. Production use would require stronger security, more robust error handling and retry policies, and asynchronous background processing.

## 3. Memorandum review with Python and Jupyter

The notebook demonstrates an individual staff member reviewing preliminary memorandums against the associated intake data and supporting documents.

It reads a local SQLite database in read-only mode, extracts PDF text, and groups each memorandum with its own intake evidence. One OpenAI Responses API request asks for a separate review of each memorandum using a strict JSON response schema. The notebook displays the reviews and exports one PDF per review.

Review instructions focus on factual discrepancies, unsupported assertions, conflicting accounts, missing evidence, and matters requiring attorney verification. This review does not perform external legal research or independently validate authorities.

The review workflow:

1. Loads memorandum records and related intake information from a local SQLite database in read-only mode.
2. Extracts text from memorandum PDFs and supporting documents.
3. Groups each memorandum with its associated intake information and supporting evidence.
4. Sends the combined data to OpenAI in a single request for a separate structured review of each memorandum.
5. Reviews factual discrepancies, unsupported assertions, conflicting accounts, missing evidence, and matters requiring attorney verification.
6. Displays the returned reviews in the Jupyter notebook and exports each review as a separate PDF.

### Running locally

1. Install Python, then install JupyterLab and the notebook’s dependencies:

   ```powershell
   python -m pip install jupyterlab python-dotenv pypdf openai fpdf2
   ```

2. Open a terminal in `project/jupyter-notebook/summarize-docs-data` and launch JupyterLab:

   ```powershell
   jupyter lab
   ```

   Open `legal-demo.ipynb` and select the Python kernel associated with the installed dependencies.

3. Create a `.env` file in that directory containing:

   ```dotenv
   OPENAI_API_KEY=your-api-key
   ```

   The notebook loads this file using `python-dotenv`. The key must have access to the configured `gpt-4.1` model.

4. Confirm that `resources/data.db` contains the demo database and that `resources/docs` contains the memorandum PDFs and supporting PDFs referenced by its records. The notebook opens the database in read-only mode; it does not create or seed it. An extraction cell explicitly accesses the first two memorandum filenames, so the current demo requires at least two memorandum records with matching PDFs.

5. Check the notebook’s database, document, and output paths. They currently use absolute paths under `D:\lawfirm-intake-project\project\jupyter-notebook\summarize-docs-data`. Adjust them if your project is elsewhere. The PDF exporter also uses `C:\Windows\Fonts\arial.ttf`; update the font path if necessary.

6. Run the cells in order to load the records, extract PDF text, assemble the review payload, request reviews from OpenAI, and display the results. The notebook uses its own database and document fixtures; running the Java application does not automatically update them.

7. Inspect the displayed reviews and the generated `memorandum-<id>-review.pdf` files in `output`. The exporter creates the output directory if needed and overwrites matching review filenames on subsequent runs. Review text is written literally, so Markdown formatting markers may appear in the PDFs.

## Scope and limitations

These are demonstrations for staff assistance, with attorney review required before relying on generated content. AI review or schema validation does not establish factual or legal accuracy. CourtListener search results are research leads, rather than verified holdings or completed legal research.

My background is in software engineering, and I do not have professional legal domain expertise. The fictional scenarios, workflow assumptions, terminology, and generated documents demonstrate technical capabilities; they would need validation and refinement with attorneys and law firm staff before practical use.

The Spring Boot endpoint processes memorandums synchronously to keep the demonstration simple and make execution easy to follow. For a deployed application, these long-running AI and research tasks would be better handled asynchronously, with background processing, job status tracking, and a way to retrieve completed results without keeping an HTTP request open.

PDF processing extracts embedded text and does not perform OCR. Scanned documents and complex layouts may require additional handling.

The Java application intentionally uses demo security settings, including seeded plain-text passwords and disabled CSRF protection. Its logging includes full fictional OpenAI prompts and response content. These settings require redesign before use with real client information or deployment beyond a controlled demonstration.

> [!WARNING]
> Keep actual API keys, `.env` files, n8n storage, backups, and sensitive logs out of Git. Running the AI stages requires API access and may incur charges.

## Author

James A. Brannan

> [!NOTE]
> Codex provided substantial assistance with coding and documentation throughout this project. The author contributed directly to the implementation and content, directed Codex’s work, and is responsible for the final result.

## License

This project is licensed under the [MIT License](LICENSE). Third-party dependencies remain subject to their respective licenses.
