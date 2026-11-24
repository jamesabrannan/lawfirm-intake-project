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

Three demonstrations explore how software and AI can assist law firm staff with intake preparation, preliminary legal research, and review of research memorandums. The examples use fictional clients, intake records, and supporting documents.

| Demo | Purpose | Implementation |
| --- | --- | --- |
| [Intake preparation](project/n8n_work) | Identify missing documents, prepare intake briefs, and draft staff handoffs | n8n, JavaScript, OpenAI, CSV and PDF files |
| [Research memorandum preparation](project/spring-boot-work/legal-research) | Select assigned work, assemble case evidence, search for authorities, and generate preliminary memorandums | Java 17, Spring Boot, Spring AI, Spring Security, SQLite, PDFBox, CourtListener |
| [Memorandum review](project/jupyter-notebook/summarize-docs-data/legal-demo.ipynb) | Compare memorandums with intake information and supporting documents | Python, Jupyter, SQLite, OpenAI, pypdf, fpdf2 |

Together, these illustrate successive staff activities. They are separate demonstrations with their own fixtures and execution steps; they do not automatically transfer records or outputs between projects.

## Watch the demonstrations

Brief screen recordings show each demo executing and producing its results. No installation is needed to view them; GitHub may offer large videos as downloads.

- [n8n intake workflow](n8n-run.mp4)
- [Java research memorandum workflow](spring-run.mp4)
- [Jupyter memorandum review](jupyter-notebook-run.mp4)

## 1. Intake preparation with n8n

The manually triggered workflow reads CSV records for clients, active intakes, law clerk assignments, and paralegal assignments. JavaScript combines the related records, checks the expected document checklist, and routes work according to the supplied status flags.

The workflow extracts text from supporting PDFs, requests an intake brief from OpenAI, and submits the draft to a separate AI review step. Briefs that pass that step are rendered as PDFs. Missing-document requests and paralegal handoff messages are saved as text files for inspection; the workflow does not send those emails.

**Main files:** [workflow export](project/n8n_work/My%20workflow.json) and [Docker Compose configuration](project/n8n_work/compose.yml).

### Running locally

1. Install Docker Desktop with Docker Compose.
2. In `project/n8n_work`, create a local `openai-key.txt` using the supplied example file. The Compose configuration loads it as an environment file containing `OPENAI_API_KEY`.
3. Supply the fictional CSV and PDF fixtures under `storage/files/resource/` and create `storage/files/outbox/`. The exported workflow reads those paths through `/files/output`. Local storage is excluded from Git, so a fresh clone may require the fixture files to be supplied separately.
4. From `project/n8n_work`, run:

   ```powershell
   docker compose up -d n8n
   ```

5. Open <http://localhost:5678>, complete the local n8n setup, and import `My workflow.json`.
6. Configure the OpenAI credentials selected by the model nodes, then execute the workflow manually. Inspect generated files under `storage/files/outbox/`.

The Compose file binds `storage/n8n` to `/home/node/.n8n`, preserving n8n's database and configuration outside the container. It also contains an additional `../resources` mount; the current workflow's CSV and source-PDF nodes use `storage/files/resource/` instead. Assignment flags come from the input fixtures; generated files should not be mistaken for a durable update to an external CRM.

## 2. Research memorandum preparation with Java

The Spring Boot application exposes a synchronous `POST /api/memorandums/prepare` endpoint. Spring Security authenticates the staff member with HTTP Basic and requires the `MEMORANDUM_PREPARER` authority. Work selection uses the authenticated identity and assigned records.

The preparation workflow:

1. Loads assigned memorandum work and related intake information from SQLite.
2. Extracts supporting PDF text while retaining page information.
3. Uses Spring AI to request a structured research plan.
4. Searches CourtListener for preliminary case leads.
5. Assesses the returned research evidence and drafts a memorandum.
6. Writes a PDF and then conditionally updates the memorandum's completion state.

The endpoint waits for processing to finish and returns a JSON summary. It does not return the PDF itself. Generated memorandums are saved under `output/memorandums/` in the application working directory.

### Running locally

- Install Java 17 or later and Maven.
- Set `OPENAI_API_KEY` and `COURTLISTENER_API_KEY` in the environment inherited by the application. Example key files are available in `project/spring-boot-work`; the application configuration itself reads environment variables.
- From `project/spring-boot-work/legal-research`, run:

  ```powershell
  mvn spring-boot:run
  ```

- Use Postman or another HTTP client to send `POST http://localhost:8080/api/memorandums/prepare` with HTTP Basic credentials for a seeded user whose role is `MEMORANDUM_PREPARER`. The fictional accounts are defined in `src/main/resources/data.sql`. No request body is required.
- Inspect the JSON completion summary and generated PDFs.

The SQLite database is created in memory from `schema.sql` and `data.sql` on every application startup. Completion state resets on restart; saved PDFs remain on disk. Research requests are deliberately paced, so a preparation request can take time.

The source includes tests for security, work selection, PDF extraction, structured AI stages, research processing, and output completion. Run them locally with `mvn test`. Creating this README did not rerun the tests or make live API requests.

## 3. Memorandum review with Python and Jupyter

The notebook demonstrates an individual staff member reviewing preliminary memorandums against the associated intake data and supporting documents.

It reads a local SQLite database in read-only mode, extracts PDF text, and groups each memorandum with its own intake evidence. One OpenAI Responses API request asks for a separate review of each memorandum using a strict JSON response schema. The notebook displays the reviews and exports one PDF per review.

Review instructions focus on factual discrepancies, unsupported assertions, conflicting accounts, missing evidence, and matters requiring attorney verification. This review does not perform external legal research or independently validate authorities.

### Running locally

1. Install Python and Jupyter, along with the notebook's dependencies:

   ```powershell
   python -m pip install jupyterlab python-dotenv pypdf openai fpdf2
   ```

2. Open `project/jupyter-notebook/summarize-docs-data/legal-demo.ipynb`.
3. Configure a local `.env` containing `OPENAI_API_KEY`.
4. Check the paths to `resources/data.db`, `resources/docs`, and `output`. The current notebook uses absolute paths under `D:\lawfirm-intake-project` and the Windows Arial font at `C:\Windows\Fonts\arial.ttf`; adjust them if using another location or operating system.
5. Run the cells in order. The API request cell sends the combined review payload to OpenAI and incurs API usage.

Review PDFs are written to the notebook project's `output/` directory. The current exporter writes the returned review text literally, so Markdown markers may appear in the PDFs. The notebook uses its own database and document fixtures; running the Java demo does not automatically refresh them.

## Scope and limitations

These are demonstrations for staff assistance, with attorney review required before relying on generated content. AI review or schema validation does not establish factual or legal accuracy. CourtListener search results are research leads, rather than verified holdings or completed legal research.

My background is in software engineering, and I do not have professional legal domain expertise. The fictional scenarios, workflow assumptions, terminology, and generated documents demonstrate technical capabilities; they would need validation and refinement with attorneys and law firm staff before practical use.

The Spring Boot endpoint processes memorandums synchronously to keep the demonstration simple and make execution easy to follow. For a deployed application, these long-running AI and research tasks would be better handled asynchronously, with background processing, job status tracking, and a way to retrieve completed results without keeping an HTTP request open.

PDF processing extracts embedded text and does not perform OCR. Scanned documents and complex layouts may require additional handling.

The Java application intentionally uses demo security settings, including seeded plain-text passwords and disabled CSRF protection. Its logging includes full fictional OpenAI prompts and response content. These settings require redesign before use with real client information or deployment beyond a controlled demonstration.

Keep actual API keys, `.env` files, n8n storage, backups, and sensitive logs out of Git. Running the AI stages requires API access and may incur charges.

## Author

James Brannan

## License

This project is licensed under the [MIT License](LICENSE). Third-party dependencies remain subject to their respective licenses.
