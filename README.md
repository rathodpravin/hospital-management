# Hospital Management System
Java 21 · Spring Boot 3 · Spring Security (JWT + BCrypt) · Spring Data JPA/Hibernate · MySQL · HTML/CSS/JS

## Architecture
`Browser (Fetch API) → JwtFilter → Controller → Service → Repository → MySQL`
Packages: `config`, `controller`, `service`, `repository`, `entity`, `dto`, `exception`, `util`.
The frontend lives in `src/main/resources/static` and is served by the same app (no CORS needed).

## Roles
| Role | Access |
|---|---|
| ADMIN | everything (doctors, patients, appointments, billing, dashboard) |
| DOCTOR | own appointments, patient list, write records/prescriptions |
| PATIENT | self-register, book/cancel own appointments, view own records, prescriptions, bills |

## Run locally
1. Install JDK 21, Maven, MySQL 8.
2. Set environment variables (PowerShell: `$env:DB_PASSWORD="..."`, Linux/Mac: `export DB_PASSWORD=...`):
   `DB_PASSWORD` (required), `DB_USERNAME` (default root), `DB_HOST`, `DB_PORT`, `DB_NAME` (default hospital_db),
   `JWT_SECRET` (32+ chars, set in production), `ADMIN_PASSWORD` (default Admin@123).
3. `mvn spring-boot:run` then open http://localhost:8080
4. Login: `admin` / `Admin@123` (change it!). Tables are created automatically by Hibernate.

## Test with Postman
Import `postman/hospital-api.postman_collection.json`, run "1. Login" first (token is saved automatically).
Use "Register patient" for a patient token. Create a doctor as admin, then log in as that doctor to test records/prescriptions.

## API summary
| Endpoint | Roles |
|---|---|
| POST /api/auth/login, /api/auth/register | public |
| GET /api/dashboard | any (role-specific data) |
| /api/doctors (GET any; POST/PUT/DELETE ADMIN) | |
| /api/patients (GET ADMIN, DOCTOR; /me PATIENT; PUT/DELETE ADMIN) | |
| /api/appointments (GET any; POST ADMIN, PATIENT; PATCH /{id}/status) | |
| /api/records, /api/prescriptions (GET any; POST DOCTOR) | |
| /api/bills (GET ADMIN, PATIENT; POST + PATCH /{id}/pay ADMIN) | |

## GitHub
```
git init
git add .
git commit -m "Initial commit: Hospital Management System"
git branch -M main
git remote add origin https://github.com/<your-username>/hospital-management.git
git push -u origin main
```

## Deploy (Render / Railway)
1. Create a cloud MySQL (Railway MySQL, Aiven, PlanetScale-compatible, etc.).
2. Create a Web Service from your GitHub repo (Docker is auto-detected from `Dockerfile`).
3. Add env vars: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `ADMIN_PASSWORD`.
4. Deploy, open the public URL, log in as admin and change the default password.
