<!--
File: AGENTS.md
Author: James Brannan
Date: 2026-11-22
Description: Instructions for work in the researchmemorandum Java project.

Copyright (c) 2026 James Brannan
Licensed under the MIT License. See the LICENSE file in the project root
for the full license terms. SPDX-License-Identifier: MIT
-->

# Project instructions

These instructions apply to this directory and all its subdirectories.

## Ask before making changes

- Always ask James Brannan before making any change, and wait for explicit approval before proceeding.
- Describe the proposed changes and affected files before asking for approval.
- Do not treat a request to discuss, review, investigate, or propose a design as permission to modify files.
- Read-only inspection may proceed without approval. Ask before commands that modify files, including builds or tests that generate output.
- Keep approved changes within the scope described. Ask again before making additional changes outside that scope.

## Headers for new files

Every newly created file must begin with a complete, readable file header stating:

- File: the actual filename, including its extension (for example, PdfDocumentService.java).
- Author: James Brannan.
- Date: the creation date in YYYY-MM-DD format.
- Description: a brief explanation of the file's purpose.
- Copyright: the applicable year and James Brannan as copyright holder.
- License statement: explicitly state that the file is licensed under the MIT License, refer to the project-root LICENSE file for the full terms, and include SPDX-License-Identifier: MIT.

Do not use an author-only comment or an abbreviated attribution in place of this header. Populate the filename, date, year, and description with actual values; do not leave template placeholders in created files.

For Java, use a Javadoc class header (`/** ... */`) immediately before the class, interface, or enum declaration and its annotations, after the package and import statements. Describe the type in the opening sentence and use the standard Javadoc tags shown below. Do not use a plain block comment as a substitute.

Use the current project version from pom.xml for @version. Use the version in which the type was introduced for @since; if that cannot be established, ask rather than inventing it.

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

For non-Java files, put the header at the beginning using XML comments for XML files, HTML comments for Markdown files, and the relevant comment syntax for other text formats. Preserve required format declarations before comments when necessary.

If a file format does not support comments, such as JSON or a binary artifact, ask how the required information should be recorded before creating the file. Do not insert an invalid header.

Do not retrofit headers into existing files without approval. Preserve third-party copyright and license notices; do not replace them with the project header.

## Logging

- Every Java class must have logging using SLF4J, with a logger associated with that class. Every method, including constructors, getters, setters, and private helpers, must log its inputs and its return value or completion. Log exceptional exits as well.
- Log major pieces of logic: work selection, significant decisions and branches, database operations, document extraction, AI stages, review outcomes, and output saving. Include relevant identifiers so a memorandum can be followed through the workflow.
- Use TRACE for routine accessor/constructor logging, DEBUG for method inputs and outputs and implementation details, INFO for major workflow milestones, WARN for recoverable problems, and ERROR for failures. Use parameterized messages rather than string concatenation.
- For collections and large objects, log safe identifiers, counts, status values, and concise summaries instead of dumping entire objects, PDF text, prompts, or AI responses. A void method should log completion rather than a return value.
- Never log passwords, API keys, Authorization headers, authentication or CSRF tokens, session cookies, SSNs, or sensitive client narratives and medical/legal details. Redact sensitive arguments and return fields; logging requirements do not authorize disclosure of those values. Sanitize untrusted text to prevent forged log entries.
- Do not swallow exceptions for logging or change application behavior solely to produce log entries. Avoid repeatedly logging the same exception at every layer.
- Apply these requirements when creating or modifying Java code. Adding logging retroactively to other existing classes requires approval under the change-control instructions above.
