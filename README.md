# Shifo - Clinic Management API

Backend for the Shifo clinic-management platform: appointment scheduling, patient and doctor records, procedures, payments, and role-based access control.

## Tech stack

- Java 21, Spring Boot 3 (Web, Security, Validation, Data JPA)
- MySQL, springdoc-openapi (Swagger UI), Lombok
- Docker / docker compose

## Architecture

Feature-based packages (`features/appointment`, `patient`, `doctor`, `finance`, ...), each with its own controllers, application services, commands, and domain logic. Cross-cutting concerns (global exception handling, security, OpenAPI) live in `config/`.

## Features

- Appointment scheduling - single & batch creation with availability checking
- Patient, doctor, specialization, and procedure management
- Payments, balances, and finance/revenue reporting
- Authentication with role- and permission-based access control

## Run

```bash
./mvnw spring-boot:run
# or
docker compose up
```

API docs: `http://localhost:8080/swagger-ui.html`
