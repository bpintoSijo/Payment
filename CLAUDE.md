# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean install

# Run application (port 8080)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=PaymentApplicationTests

# Package without tests
./mvnw clean package -DskipTests
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Architecture

This is a Spring Boot 4.0.x / Java 21 payment processing application using the **Strategy pattern** for payment methods.

### Core Domain (`src/main/java/com/payments/payments/`)

- `Payment` — interface defining the contract: `pay(BigDecimal amount)` and `getType()`
- `AbstractPaymentMethod` — base class implementing shared logic: null-amount validation, negative-amount refusal, and payment output. New payment methods should extend this.
- `CreditCardPayment`, `PaypalPayment`, `CryptoPayment` — concrete implementations

### Transaction Model (`src/main/java/com/payments/transactions/`)

- `Transaction` — a Java record (`id`, `amount`, `payment`) representing a single payment transaction. Immutable by design.

### Infrastructure

- **Database**: H2 (file-based at `./data/mydata`) for local/dev; commented-out MySQL config in `application.yml` for production reference. Schema is `create-drop` on startup.
- **H2 Console**: available at `http://localhost:8080/h2-console` when running locally.
- **OpenAPI/Swagger**: SpringDoc 3.0.2 is on the classpath — Swagger UI will be available at `/swagger-ui.html` once REST controllers are added.
- **JPA**: `@EnableJpaRepositories` is active; no repositories exist yet — add them under an appropriate package.

### Extending the project

- **New payment method**: extend `AbstractPaymentMethod`, implement `getType()`, annotate as a Spring `@Component` if needed.
- **REST layer**: no controllers exist yet; add them under `com.payments` following standard Spring MVC conventions.
- **Persistence**: add Spring Data JPA `@Repository` interfaces to persist `Transaction` entities.