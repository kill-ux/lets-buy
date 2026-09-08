# Let's Play

A RESTful CRUD API for a small e-commerce-like platform, built with **Spring Boot** and **MongoDB**. Admins manage all users and products; regular users manage only their own products. Authentication is JWT-based, and revoked/deleted users are locked out immediately via a Redis-backed token blacklist.

## Tech Stack

- **Spring Boot 4.1.1** (Java 26, Gradle)
- **Spring Data MongoDB** — persistence
- **Spring Security** — stateless JWT authentication + role-based access control
- **jjwt** — JWT generation/validation
- **Redis** — per-user token revocation (deleting a user or changing their role/password invalidates their existing tokens immediately, instead of waiting for expiry)
- **Bean Validation** (`jakarta.validation`) — request validation
- **BCrypt** — password hashing
- **Lombok** — boilerplate reduction

## Prerequisites

- Java 26 (or adjust `build.gradle`'s toolchain version to match your JDK)
- Docker (for MongoDB and Redis)

## Setup

### 1. Environment variables

Copy the example file and fill in real values:

```sh
cp .env.example .env
```

| Variable                                                    | Description                                                                             |
| ----------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| `MONGO_HOST` / `MONGO_PORT`                                 | MongoDB connection details                                                              |
| `MONGO_DATABASE`                                            | Database name                                                                           |
| `MONGO_INITDB_ROOT_USERNAME` / `MONGO_INITDB_ROOT_PASSWORD` | MongoDB root credentials (used by both the app and the Docker container)                |
| `JWT_SECRET`                                                | Secret key used to sign JWTs — use a long, random string                                |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD`                            | Credentials for the default admin account, auto-created on first run if no admin exists |
| `REDIS_PASSWORD`                                            | Password for the Redis container                                                        |
| `SSL_KEYSTORE_PASSWORD`                                     | Password protecting the local HTTPS keystore                                            |

### 2. Start MongoDB and Redis

```sh
docker compose up -d
```

### 3. Generate a local HTTPS certificate

The app serves HTTPS only (with an HTTP→HTTPS redirect on port 8080). Generate a self-signed keystore once:

```sh
keytool -genkeypair \
  -alias letsplay \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore src/main/resources/keystore.p12 \
  -validity 3650 \
  -storepass changeit \
  -dname "CN=localhost, OU=LetsPlay, O=LetsPlay, L=City, S=State, C=US"
```

Make sure `-storepass` matches `SSL_KEYSTORE_PASSWORD` in your `.env`.

> Since the certificate is self-signed, your browser/HTTP client will warn about it. For `curl`, pass `-k`. In a browser, accept the security warning to proceed. In Postman, disable SSL certificate verification in Settings → General.

### 4. Run the application

```sh
./gradlew bootRun
```

The API is served at `https://localhost:8443`. Plain HTTP requests to `http://localhost:8080` are automatically redirected to HTTPS.

On first run, if no admin account exists yet, one is created automatically using `ADMIN_EMAIL` / `ADMIN_PASSWORD` from your `.env`.

## Testing

A Postman collection covering the full functional and security test pass (registration, login, ownership checks, RBAC, validation errors, not-found handling) is included at `postman/lets-buy.postman_collection.json`. Import it into Postman, set `baseUrl` to `https://localhost:8443`, and run it top to bottom.

## Roles

| Role                             | Permissions                                                       |
| -------------------------------- | ----------------------------------------------------------------- |
| `USER` (default on registration) | Create products; read, update, and delete only their own products |
| `ADMIN`                          | Full access to all users and all products                         |

## API Endpoints

### Auth

| Method | Endpoint         | Access | Description                                           |
| ------ | ---------------- | ------ | ----------------------------------------------------- |
| POST   | `/auth/register` | Public | Register a new user (always created with role `USER`) |
| POST   | `/auth/login`    | Public | Log in, returns a JWT + role                          |

### Users

| Method | Endpoint      | Access     | Description                                      |
| ------ | ------------- | ---------- | ------------------------------------------------ |
| GET    | `/users`      | Admin only | List all users                                   |
| GET    | `/users/{id}` | Admin only | Get a single user                                |
| PUT    | `/users/{id}` | Admin only | Update a user                                    |
| DELETE | `/users/{id}` | Admin only | Delete a user (revokes their outstanding tokens) |

### Products

| Method | Endpoint                | Access         | Description                            |
| ------ | ----------------------- | -------------- | -------------------------------------- |
| GET    | `/products`             | Public         | List all products                      |
| GET    | `/products/{id}`        | Public         | Get a single product                   |
| GET    | `/products/{id}/manage` | Owner only     | Owner-restricted view of a product     |
| POST   | `/products`             | Authenticated  | Create a product (owned by the caller) |
| PUT    | `/products/{id}`        | Owner or admin | Update a product                       |
| DELETE | `/products/{id}`        | Owner or admin | Delete a product                       |

## Example Requests

### Register

```sh
curl -k -X POST https://localhost:8443/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@example.com","password":"password123"}'
```

### Login

```sh
curl -k -X POST https://localhost:8443/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"password123"}'
```

### Create a product (authenticated)

```sh
curl -k -X POST https://localhost:8443/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Chess Set","description":"Wooden","price":25.5}'
```

## Security Notes

- Passwords are hashed with BCrypt before storage; the raw password is never persisted.
- API responses never include the password field, even hashed (enforced via a dedicated response DTO for users).
- All endpoints except `GET /products`, `GET /products/{id}`, and `/auth/**` require a valid JWT.
- Deleting a user, or changing their role or password, immediately invalidates any JWTs they were issued (checked against Redis on every request), even though the JWTs themselves remain cryptographically valid until their natural expiry.
- All traffic is served over HTTPS; plain HTTP requests are redirected.
- Global exception handling ensures no endpoint returns an unhandled `5xx` error or a raw stack trace — all errors return a consistent JSON shape with an appropriate status code.
