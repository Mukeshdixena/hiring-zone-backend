# HiringZone Backend

Spring Boot 3 REST API powering the HiringZone job portal.

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security (JWT, RBAC)
- Spring Data JPA + Hibernate
- PostgreSQL
- Redis (session/cache)
- Flyway (database migrations)
- Swagger / OpenAPI 3

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 15 running on `localhost:5432`
- Redis 7 running on `localhost:6379`

### Run

```bash
mvn spring-boot:run
```

The API starts on **port 9090**.  
Swagger UI: http://localhost:9090/swagger-ui.html

### Build fat JAR

```bash
mvn package -DskipTests
java -jar target/hiring-zone-backend-*.jar
```

## Environment Variables

Variables can be placed in a local `.env` file. Empty `.env` values are ignored so defaults still work.

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/hiringzone` | Full database URL. Accepts `jdbc:postgresql://...`, `postgresql://...`, or `postgres://...` and takes priority over `DB_HOST`, `DB_PORT`, and `DB_NAME` when set. |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `hiringzone` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASSWORD` | `password` | Database password |
| `REDIS_HOST` | `localhost` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |
| `JWT_SECRET` | (built-in 64-char hex) | JWT signing secret — **always override in production** |
| `SUPER_ADMIN_EMAIL` | `admin@hiringzone.com` | Bootstrap admin account email |
| `SUPER_ADMIN_PASSWORD` | `admin123` | Bootstrap admin password — **always override in production** |
| `ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated CORS allowed origins |
| `PORT` | `9090` | HTTP port. Most hosting platforms provide this automatically. |

## Deployment

Use Java 17 and Maven.

```bash
mvn package -DskipTests
java -jar target/hiring-zone-backend-0.0.1-SNAPSHOT.jar
```

Set `DB_URL`, `JWT_SECRET`, `SUPER_ADMIN_EMAIL`, `SUPER_ADMIN_PASSWORD`, and `ALLOWED_ORIGINS` in the hosting platform environment. `ALLOWED_ORIGINS` should include the deployed frontend origin, for example `https://mukeshdixena.github.io`. If you paste the full GitHub Pages URL with `/hiring-zone`, the backend normalizes it automatically.

### Render

Render deploys JVM apps with Docker. Create the service as a Docker Web Service and leave the Dockerfile path as `Dockerfile`.

Set these environment variables in Render:

```env
DB_URL=
JWT_SECRET=
SUPER_ADMIN_EMAIL=
SUPER_ADMIN_PASSWORD=
ALLOWED_ORIGINS=https://mukeshdixena.github.io
```

## API Endpoints

### Public (no auth required)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/register` | Seeker registration |
| `POST` | `/api/auth/login` | Seeker login |
| `POST` | `/api/employer/auth/register` | Employer registration |
| `POST` | `/api/employer/auth/login` | Employer login |
| `POST` | `/api/admin/auth/login` | Admin login |
| `GET` | `/api/jobs` | Search jobs (keyword, location, category, types, expLevels, minSalary, sort, page, size) |
| `GET` | `/api/jobs/{id}` | Job detail |
| `GET` | `/api/meta` | Job types, experience levels, categories, popular tags |
| `GET` | `/api/stats/public` | Platform statistics |

### Seeker (Bearer token — ROLE_SEEKER)

| Method | Path | Description |
|--------|------|-------------|
| `GET/PUT` | `/api/profile` | Seeker profile |
| `POST` | `/api/jobs/{id}/apply` | Apply to job |
| `GET` | `/api/applications` | My applications (with optional `status` filter) |
| `GET` | `/api/applications/stats` | Application statistics |
| `GET` | `/api/saved-jobs` | Saved jobs |
| `POST/DELETE` | `/api/jobs/{id}/save` | Save / unsave job |

### Employer (Bearer token — ROLE_EMPLOYER)

| Method | Path | Description |
|--------|------|-------------|
| `GET/POST` | `/api/employer/jobs` | List / create jobs |
| `PUT/DELETE` | `/api/employer/jobs/{id}` | Update / delete job |
| `GET` | `/api/employer/jobs/{id}/applications` | Applications for a job |
| `PATCH` | `/api/employer/applications/{id}/status` | Update application status |
| `GET` | `/api/employer/stats` | Employer dashboard statistics |

### Admin (Bearer token — ROLE_ADMIN)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/admin/users` | All users |
| `PATCH` | `/api/admin/users/{id}/suspend` | Suspend / unsuspend user |
| `GET` | `/api/admin/providers` | All employers/companies |
| `PATCH` | `/api/admin/providers/{id}/verify` | Verify employer |
| `GET` | `/api/admin/jobs` | All jobs (admin view) |
| `PATCH` | `/api/admin/jobs/{id}/flag` | Flag / unflag job |
| `PATCH` | `/api/admin/jobs/{id}/expire` | Expire job |
| `DELETE` | `/api/admin/jobs/{id}` | Delete job |
| `POST` | `/api/admin/announcements` | Post announcement |
| `GET` | `/api/admin/stats` | Platform statistics |
| `GET` | `/api/admin/activity` | Recent activity feed |

## Database Migrations

Flyway manages schema versioning. Migration scripts are in `src/main/resources/db/migration/`.

| Version | Description |
|---------|-------------|
| V1 | Initial schema (users, companies, jobs, applications, saved_jobs, announcements, admin_logs) |
| V2 | Add seeker_profile tables (profiles, experiences, educations) |
