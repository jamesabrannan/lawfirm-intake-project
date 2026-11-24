<!--
File: AGENTS.md
Author: James Brannan
Date: 2026-11-22
Description: Repository instructions for the n8n, Java, and Jupyter demonstrations.

Copyright (c) 2026 James Brannan
Licensed under the MIT License. See the LICENSE file in the project root
for the full license terms. SPDX-License-Identifier: MIT
-->

# Project instructions

These instructions apply to this repository and all its subdirectories. Java-specific requirements are scoped explicitly below; they do not impose Java tooling or conventions on the n8n or Python demonstrations.

## Python and Jupyter notebooks

When working with Python in a Jupyter notebook, paste the code and output in the chat instead of adding them to or modifying the notebook, unless the user explicitly requests notebook edits.

## Ask before making changes

- Always ask James Brannan before making any change, and wait for explicit approval before proceeding.
- An explicit instruction to create, edit, delete, or fix specified files or content is approval for that scope; do not ask for the same approval again. If the requested change or its scope is unclear, describe the proposed changes and ask before proceeding.
- Describe the proposed changes and affected files before asking for approval.
- Do not treat a request to discuss, review, investigate, or propose a design as permission to modify files.
- Read-only inspection may proceed without approval. Ask before commands that modify files, including builds or tests that generate output.
- Keep approved changes within the scope described. Ask again before making additional changes outside that scope.

## Headers for new Java files only

The header requirements in this section apply only to newly created `.java` files. They do not apply to Markdown, Python, notebooks, JSON, XML, SQL, configuration files, images, videos, or other non-Java files. Do not add headers to non-Java files or request header-related approval for those files.

Every newly created Java file must include a complete, readable Javadoc type header stating:

- File: the actual filename, including its extension (for example, PdfDocumentService.java).
- Author: James Brannan.
- Date: the creation date in YYYY-MM-DD format.
- Description: a brief explanation of the file's purpose.
- Copyright: the applicable year and James Brannan as copyright holder.
- License statement: explicitly state that the file is licensed under the MIT License, refer to the project-root LICENSE file for the full terms, and include SPDX-License-Identifier: MIT.

Do not use an author-only comment or an abbreviated attribution in place of this header. Populate the filename, date, year, and description with actual values; do not leave template placeholders in created files.

Use a Javadoc type header (`/** ... */`) immediately before the class, interface, enum, or record declaration and its annotations, after the package and import statements. Describe the type in the opening sentence and use the standard Javadoc tags shown below. Do not use a plain block comment as a substitute.

Use the current version from the Java file's owning Maven project's `pom.xml` for @version. Use the version in which the type was introduced for @since; if that cannot be established, ask rather than inventing it. The template version below is illustrative; verify the actual version before use.

Use standard tags @author, @version, and @since. Use custom tags @file, @date, @copyright, and @license for the remaining metadata, each on its own line. These four tags are project conventions, not built-in Javadoc tags; if generating Javadoc, register them using the Javadoc tool's custom-tag configuration. Keep the brief description as the opening prose, matching a conventional Java class header; do not invent an @description tag.

Java header template:

```java
/**
 * Briefly describes the purpose and responsibility of this class.
 *
 * @file ClassName.java
 * @author James Brannan
 * @date YYYY-MM-DD
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) YYYY James Brannan.
 * @license MIT License. See LICENSE in the project root for the full terms.
 *          SPDX-License-Identifier: MIT
 */
```

Do not retrofit headers into existing Java files without approval. Preserve existing third-party copyright and license notices in all files; do not replace them with the project header.

## Java application logging

This section and the two logging sections that follow apply only to the Java application under `project/spring-boot-work/legal-research/`. They do not require SLF4J, POJO conversion, or the Java HTTP logger in n8n or Python. Credential protection still applies throughout the repository.

- Every Java class must have logging using SLF4J, with a logger associated with that class. Every method, including constructors, getters, setters, and private helpers, must log its inputs and its return value or completion. Log exceptional exits as well.
- Log major pieces of logic: work selection, significant decisions and branches, database operations, document extraction, AI stages, review outcomes, and output saving. Include relevant identifiers so a memorandum can be followed through the workflow.
- Use TRACE for routine accessor/constructor logging, DEBUG for method inputs and outputs and implementation details, INFO for major workflow milestones, WARN for recoverable problems, and ERROR for failures. Use parameterized messages rather than string concatenation.
- For collections and large objects, log safe identifiers, counts, status values, and concise summaries instead of dumping entire objects, PDF text, prompts, or AI responses. A void method should log completion rather than a return value.
- Never log passwords, API keys, Authorization headers, authentication or CSRF tokens, session cookies, or SSNs. Redact sensitive client narratives and medical/legal details except for the narrowly authorized fictional-demo OpenAI prompt/response logging below. Logging requirements do not otherwise authorize disclosure of sensitive values. Sanitize untrusted text to prevent forged log entries.
- Do not swallow exceptions for logging or change application behavior solely to produce log entries. Avoid repeatedly logging the same exception at every layer.
- Apply these requirements when creating or modifying Java code. Adding logging retroactively to other existing classes requires approval under the change-control instructions above.

## Java demo: OpenAI request and response logging

- Every OpenAI call must log the full prompt text sent (system and user messages,
  including generated JSON-schema/output-format instructions) in a separate DEBUG
  entry, followed by the returned response JSON in its own DEBUG entry.
- Log response content before POJO conversion and application validation so a
  rejected response remains inspectable. Do not substitute a POJO summary for the
  actual response. If a call fails before a response arrives, log the failure type;
  never fabricate a response entry.
- This explicitly authorized demo exception permits prompt/input and model-output
  content logging despite the general summary/redaction guidance above. It applies
  to the fictional demo data; it does not authorize logging credentials, API keys,
  Authorization headers, cookies, tokens, or transport request envelopes.
- Keep prompt text and response JSON separate: no combined request/response object.
  Log via the existing SLF4J configuration so entries reach console and file.

## Java application: external HTTP transport metadata logging

- Every external HTTP client must log each outbound attempt and its response,
  including SDK retries, using the shared safe HTTP metadata logger.
- Log a local correlation ID, provider, HTTP method, destination scheme/host/port
  and path (omit user info, query values and fragments), safe request headers,
  response status, safe response headers, and elapsed milliseconds to response
  headers. Log network/timeout failure types and elapsed time when no response arrives.
- These transport entries must never contain request/response payloads. Keep the
  separately authorized OpenAI prompt/JSON logs distinct from HTTP metadata logs.
- Header logging must use an explicit allowlist: content metadata, request IDs,
  retry/timeout metadata, and rate-limit headers. Never dump all headers. Exclude
  Authorization, Proxy-Authorization, cookies, API keys and unknown headers.
- Sanitize CR/LF and bound logged header/path lengths. Do not consume, buffer,
  change or close response bodies just to log metadata. Preserve exceptions,
  timeout settings, and retry behavior. Future external clients must use the
  same policy; do not globally enable unrestricted HTTP wire logging.
