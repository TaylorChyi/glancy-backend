# Glancy Backend

Glancy Backend is a Spring Boot service that powers the Glancy dictionary application. It exposes REST endpoints for user management, notifications, FAQs and contact messages.

## Requirements

- Java 17
- MySQL database
- Maven (the project includes the `mvnw` wrapper)

## Setup

1. Clone this repository.
2. Create a `.env` file with `DB_PASSWORD`, `thirdparty.deepseek.api-key` and
   `thirdparty.openai.api-key` values.
3. Ensure MySQL is running with a database named `glancy_db`, matching credentials, and SSL certificates configured as `useSSL=true` requires.
4. Use the `local` Spring profile if SSL is unavailable: `./mvnw spring-boot:run -Dspring.profiles.active=local`.
5. Configure optional API base URLs in `application.yml` under the `thirdparty` section.
Ensure the MySQL server provides SSL certificates trusted by the JVM. Configure the truststore if necessary when running with `useSSL=true`.
6. Properties under `search.limit` and `oss` are bound to `SearchProperties` and `OssProperties`.
   `search.limit.nonMember` controls the daily search limit for non-members (default `10`).

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
java -jar target/glancy-backend.jar
```

## Running Tests

```bash
./mvnw test
```
## Quick API Check
Example curl commands live in `CURL_TESTS.md`. Run `scripts/curl-tests.sh` to call common endpoints.
For prompt templates used when querying models, see `docs/PROMPT_CN.md`.
English prompt guidance can be found in `PROMPT_GUIDE_EN.md`.
Bilingual prompt instructions are available in `docs/PROMPT_BILINGUAL.md`.
## API Endpoints


### Health
- `GET /api/ping` – verify that the service is running

### Users
- `POST /api/users/register` – register a new user
- `DELETE /api/users/{id}` – logically delete a user
- `GET /api/users/{id}` – fetch user details
- `POST /api/users/login` – user login (send `account` and `password`)
- 登录成功后将返回 `token`，后续需要在 `X-USER-TOKEN` 请求头中携带此值
- `POST /api/users/{id}/logout` – invalidate the login token
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
- `DELETE /api/search-records/user/{userId}/{recordId}` – delete a specific search record of the user
- `DELETE /api/search-records/user/{userId}` – clear all search records of the user
- `DELETE /api/search-records/user/{userId}/{recordId}/favorite` – unfavorite a search record
  以上接口均需在 `X-USER-TOKEN` 请求头中提供登录令牌



