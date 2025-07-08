# Repository Guidelines

This project is a Spring Boot backend. Contributors should follow these rules:

## General
- Use 4 spaces for indentation in Java and YAML files.
- Keep lines under 120 characters where possible.
- Do not remove existing Lombok annotations.

## Testing
- Run `./mvnw test` before every commit. Ensure tests pass when possible.
- If the Maven wrapper fails due to environment issues, state this in the PR.

## Pull Requests
- Summarize major changes and reference modified files.
- Include the Maven test results or failure reason in the PR body.

