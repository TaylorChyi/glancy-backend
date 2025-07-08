# glancy-backend

This project provides a simple Spring Boot backend with REST APIs for user management, notifications and FAQs.

## FAQ Endpoints

- `POST /api/faqs` – create a new FAQ
- `GET /api/faqs` – list all FAQs

## Contact Us Endpoint

Submit contact messages via:

```
POST /api/contact
```
The endpoint accepts `name`, `email` and `message` fields and returns the saved record.

## Search Record Endpoints

- `POST /api/search-records/user/{userId}` – add a new search record for the user
- `GET /api/search-records/user/{userId}` – list search records of the user
- `DELETE /api/search-records/user/{userId}` – clear all search records of the user

