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

## API Development
- 每新增一个 API，都必须编写相应的单元测试。
- 更新 `scripts/curl-tests.sh` 等脚本，以便通过 curl 对新接口进行验证。
- 提交信息建议使用中文描述；如需创建分支，feature 名称应使用英文。

