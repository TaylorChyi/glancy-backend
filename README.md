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