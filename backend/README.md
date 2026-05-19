# PharmaConnect API (Spring Boot)

## Prerequisites

- Java 17+
- Maven 3.8+
- XAMPP MySQL running on port **3306**
- Database `pharma_connect` created (import `../pharma_connect.sql` in phpMyAdmin)

## Configuration

`src/main/resources/application.properties`:

- URL: `jdbc:mysql://localhost:3306/pharma_connect`
- User: `root`
- Password: *(empty)*
- Port: **8080**

## Run

```bash
cd backend
mvn spring-boot:run
```

On first start, sample data is inserted if tables are empty.

**Demo password for all seeded users:** `password123`

| Role | Email |
|------|-------|
| Patient | ahmed@pharmaconnect.tn |
| Patient | sana@pharmaconnect.tn |
| Pharmacien | youssef@pharmaciecentrale.tn |
| Admin | admin@pharmaconnect.tn |

## Frontend

Open Apache: `http://localhost/PharmaConnect/index.html`

The UI calls `http://localhost:8080/api/`.
