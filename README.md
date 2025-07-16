# Glancy Backend

Glancy Backend is a Spring Boot service that powers the Glancy dictionary application. It exposes REST endpoints for user management, notifications, FAQs and contact messages.

## Requirements

- Java 17
- MySQL database
- Maven (the project includes the `mvnw` wrapper)

## Setup

1. Clone this repository.
2. Provide a `DB_PASSWORD` value via a `.env` file or environment variable.
3. Ensure MySQL is running with a database named `glancy_db`, matching credentials, and SSL certificates configured as `useSSL=true` requires.
4. Use the `local` Spring profile if SSL is unavailable: `./mvnw spring-boot:run -Dspring.profiles.active=local`.
5. Configure optional API base URLs in `application.yml` under the `thirdparty` section.
Ensure the MySQL server provides SSL certificates trusted by the JVM. Configure the truststore if necessary when running with `useSSL=true`.
6. `search.limit.nonMember` sets the daily search limit for non-members (default `10`).

## Database Initialization

Run the schema script to create the required tables:
```bash
mysql -u glancy_user -p glancy_db < src/main/resources/sql/schema.sql
```

## Building and Running

Start the application with:

```bash
./mvnw spring-boot:run
```

Or build a jar:

```bash
./mvnw clean package
java -jar target/glancy-backend-0.0.1-SNAPSHOT.jar
```

## Running Tests

```bash
./mvnw test
```
## Quick API Check
Example curl commands live in `CURL_TESTS.md`. Run `scripts/curl-tests.sh` to call common endpoints.
For prompt templates used when querying models, see `docs/PROMPT_CN.md`.
Japanese prompt guidance can be found in `PROMPT_GUIDE_JA.md`. 
German prompt guidance is available in `PROMPT_GUIDE_DE.md`.
English prompt guidance can be found in `PROMPT_GUIDE_EN.md`.
## API Endpoints


### Health
- `GET /api/ping` – verify that the service is running

### Users
- `POST /api/users/register` – register a new user
- `DELETE /api/users/{id}` – logically delete a user
- `GET /api/users/{id}` – fetch user details
- `POST /api/users/login` – user login
- 登录成功后将返回 `token`，后续需要在 `X-USER-TOKEN` 请求头中携带此值
- `POST /api/users/{id}/third-party-accounts` – bind a third‑party account (returns the bound account)
- `GET /api/users/count` – total number of active users

### Notifications
- `POST /api/notifications/system` – create a system notification
- `POST /api/notifications/user/{userId}` – create a user notification
- `GET /api/notifications/user/{userId}` – list notifications for a user

### FAQs
- `POST /api/faqs` – create a new FAQ
- `GET /api/faqs` – list all FAQs

### User Preferences
- `POST /api/preferences/user/{userId}` – save preferences for a user
- `GET /api/preferences/user/{userId}` – fetch preferences for a user

### Contact
- `POST /api/contact` – submit a contact message

## Search Record Endpoints

- `POST /api/search-records/user/{userId}` – add a new search record for the user
- `GET /api/search-records/user/{userId}` – list search records of the user
- `DELETE /api/search-records/user/{userId}` – clear all search records of the user
  以上接口均需在 `X-USER-TOKEN` 请求头中提供登录令牌


### Portal
- `POST /api/portal/parameters` – create or update a runtime parameter
- `GET /api/portal/parameters/{name}` – get the value of a parameter
- `GET /api/portal/parameters` – list all parameters
- `GET /api/portal/user-stats` – fetch overall user counts
- `POST /api/portal/alert-recipients` – add an alert email address
- `GET /api/portal/alert-recipients` – list alert email addresses
- `PUT /api/portal/alert-recipients/{id}` – update an alert email address
- `DELETE /api/portal/alert-recipients/{id}` – remove an alert email address
- `GET /api/portal/daily-active` – daily active users and rate

## ChatGPT Prompt

When requesting French definitions, the backend sends the following instruction to GPT:

```
Vous \u00eates un assistant de dictionnaire.
Fournis la d\u00e9finition de '<terme>' en fran\u00e7ais au format:
D\u00e9finition: ...
Synonymes: ...
Les synonymes doivent \u00eatre s\u00e9par\u00e9s par des virgules.
```

This format ensures stable parsing of the returned text.

## 版本管理

项目版本号定义在 `pom.xml` 中。默认合并时只递增补丁版本号。若提升中版本号，请在 PR 中说明，GitHub Actions 会在检测到中版本号变更时自动部署。

