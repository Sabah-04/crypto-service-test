#Crypto Recommendation Service

# Crypto API

## Overview
REST API for cryptocurrency statistics, daily highest normalized crypto, and sorted normalized ranges.  
Includes **Swagger/OpenAPI docs** and standardized JSON error handling.

---

## Endpoints

| Endpoint | Method | Description |
|----------|-------|------------|
| `/crypto/stats` | GET | Crypto stats by symbol (`CryptoStatsDto`) |
| `/crypto/highest` | GET | Highest normalized crypto for a day (`CryptoNormDto`) |
| `/crypto/sorted` | GET | All cryptos sorted by normalized range (`List<CryptoNormDto>`) |

---

## Request Parameters

- `symbol` — crypto symbol (e.g., BTC, ETH)
- `day` — date in `yyyy-MM-dd`

---

## Error Handling

- Validation errors return a **field-error map**.
- All other exceptions return **`ErrorResponseDto`** with `status`, `error`, `message`, `timestamp`.

---

## Swagger UI

Access API docs at:

http://localhost:8080/swagger-ui.html


OpenAPI JSON spec:

http://localhost:8080/v3/api-docs

---

## Run Application

```bash
mvn clean install
mvn spring-boot:run
