# gemimeg-backend

PTB DCC XML/JSON/PDF conversion service.  
Converts Digital Calibration Certificate documents between XML (PTB DCC schema v3.3.0), JSON, HTML, and PDF.

Used by `dcc-service` to convert DCC JSON → XML and JSON → PDF for signing and download, and to convert calibration-pipeline XML output → JSON for saving into the DCC database.

---

## Endpoints

| Method | Path | Consumes | Produces | Description |
|---|---|---|---|---|
| `POST` | `/api/v1/dcc/xsd/dcc/xml` | `application/json` | `application/xml` | JSON → XML (validate + convert) |
| `POST` | `/api/v1/dcc/xsd/dcc/json` | `application/xml` | `application/json` | XML → JSON (validate + convert) |
| `POST` | `/api/v1/dcc/xsd/dcc/html` | `application/json` | `text/html` | JSON → HTML (via XSLT) |
| `POST` | `/api/v1/dcc/xsd/dcc/pdf` | `application/json` | `application/pdf` | JSON → PDF (via XSL-FO) |
| `GET`  | `/api/v1/dcc/xsd/dcc/{id}` | — | `application/json` | Retrieve stored DCC as JSON |
| `GET`  | `/api/v1/dcc/xsd/dcc/{id}/xml` | — | `application/xml` | Retrieve stored DCC as XML |

**No authentication required** — gemimeg-backend has no Spring Security. All endpoints are open.

---

## Requirements

- Java 25 (Eclipse Temurin)
- Maven 3.9+
- Port **10001** (default; configurable via `SERVER_PORT`)

---

## Build

> **Important**: this is a **multi-module Maven project**.
> The parent module (`gemimeg-backend/`) has no `spring-boot-maven-plugin` — all build and run commands must be executed from the **sub-module** directory:
> ```
> backend/gemimeg-backend/gemimeg-backend/   ← correct working directory
> backend/gemimeg-backend/                   ← parent pom only, do NOT run spring-boot:run here
> ```

Build from the sub-module:

```powershell
cd backend\gemimeg-backend\gemimeg-backend
mvn clean package -DskipTests
```

The runnable JAR is produced at:
```
backend\gemimeg-backend\gemimeg-backend\target\gemimeg-backend.jar
```

---

## Run locally (development)

### With `auth-debug` profile (recommended)

The `auth-debug` profile:
- Stores the H2 database at `./data/gemimeg-backend` (local path) instead of `/app/data/gemimeg-backend` (Docker path)
- Opens the H2 web console to all hosts
- Sets `de.ptb.common.dcc` logging to DEBUG

```powershell
# MUST be run from the sub-module directory, NOT the parent
cd backend\gemimeg-backend\gemimeg-backend

mvn spring-boot:run -Dspring-boot.run.profiles=auth-debug
```

Or with the pre-built JAR:

```powershell
cd backend\gemimeg-backend\gemimeg-backend

java -jar target\gemimeg-backend.jar --spring.profiles.active=auth-debug
```

The service starts on **http://localhost:10001**.

> **Common mistake**: running `mvn spring-boot:run` from `backend/gemimeg-backend/` (the parent)
> gives `No plugin found for prefix 'spring-boot'`. Always `cd` into `gemimeg-backend/gemimeg-backend/` first.

### Verify it works

```powershell
# Health check
curl http://localhost:10001/actuator/health
# Expected: {"status":"UP"}

# XML → JSON conversion (uses a generated DCC XML as input)
curl -X POST http://localhost:10001/api/v1/dcc/xsd/dcc/json `
  -H "Content-Type: application/xml" `
  --data-binary "@..\..\calibration\certificato_out\ntc_calibration_certificate.xml"

# JSON → XML conversion (reverse direction)
# Pipe the JSON output of the previous command back:
curl -X POST http://localhost:10001/api/v1/dcc/xsd/dcc/xml `
  -H "Content-Type: application/json" `
  -d "<paste JSON output from above>"
```

### H2 Console (auth-debug only)

Available at: **http://localhost:10001/h2-console**

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/gemimeg-backend` |
| Username | `sa` |
| Password | *(leave empty)* |

---

## dcc-service integration

`dcc-service` calls gemimeg-backend via the `gemimeg.backend.url` property.

| Context | Value |
|---|---|
| Local dev | `http://localhost:10001` (set in `dcc-service/application-auth-debug.properties`) |
| Docker Compose | `http://gemimeg-backend:10001` (default in `application.properties`) |

The `auth-debug` profile of dcc-service already sets `gemimeg.backend.url=http://localhost:10001` — no extra env var needed.

### Correct local dev launch order

```powershell
# Terminal 1 — start gemimeg-backend first
cd backend\gemimeg-backend\gemimeg-backend
mvn spring-boot:run -Dspring-boot.run.profiles=auth-debug
# Starts on http://localhost:10001

# Terminal 2 — start dcc-service
cd backend\dcc_service
mvn spring-boot:run -Dspring-boot.run.profiles=auth-debug
# Starts on http://localhost:8080
# Will call gemimeg at http://localhost:10001 for XML/JSON/PDF conversion
```

---

## Docker / Docker Compose

gemimeg-backend is included in `backend/compose/compose.yaml` as `gemimeg-backend` service.

Build the image first (from `backend/gemimeg-backend/`):

```bash
# From repo root
docker build -t gemimeg-backend:local backend/gemimeg-backend/

# Or let compose build it automatically
cd backend/compose
docker compose build gemimeg-backend
docker compose up gemimeg-backend
```

The service runs at **http://gemimeg-backend:10001** inside the Docker network.  
H2 data is persisted in the named volume `gemimeg_h2_data`.

---

## Configuration

All values can be overridden via environment variables:

| Property | Env var | Default (Docker) | Default (auth-debug) |
|---|---|---|---|
| `server.port` | `SERVER_PORT` | `10001` | `10001` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:h2:file:/app/data/gemimeg-backend` | `jdbc:h2:file:./data/gemimeg-backend` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `sa` | `sa` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | *(empty)* | *(empty)* |
| `logging.level.root` | `LOGGING_LEVEL_ROOT` | `INFO` | `INFO` |
| `logging.level.de.ptb.common.dcc` | — | — | `DEBUG` (auth-debug only) |

---

## Notes

- **No authentication** — all endpoints are intentionally open. gemimeg is a backend-to-backend conversion service, not exposed to the public internet directly.
- The `common.security.enabled` property in `application.yml` is tied to `server.ssl.enabled` (both false by default) — this controls an optional SSL-based security layer in the `gemimeg-backend-api` module, not Spring Security.
- DCC persistence (`common.dcc.persist-enabled`) is set to `false` by default — converted documents are returned directly without being stored in H2.
- The H2 console (`/h2-console`) is enabled in all profiles; in Docker it is accessible only from within the container unless you port-forward.
