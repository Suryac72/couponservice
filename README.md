# Monk Commerce Coupon Service

This repository contains the backend RESTful API for a coupon management and application system, built as part of the **2025 Monk Commerce backend developer task**.

The system is built as a **production-ready, extensible Spring Boot microservice**, fully containerized with Docker.
# Monk Commerce Coupon Service

This repository contains the backend RESTful API for a coupon management and application system, built as part of the 2025 Monk Commerce backend developer task.

The system is built as a production-ready, extensible Spring Boot microservice, fully containerized with Docker.

## 1. Core Objective

To build a RESTful API to manage and apply three types of discount coupons (cart-wise, product-wise, and BxGy) for an e-commerce platform, with a core focus on extensibility for future coupon types.

## 2. Tech Stack & Design Principles

To meet the "real-world implementation" and "extensibility" requirements, the following stack and design patterns were chosen:

- **Java 17 & Spring Boot 3**: Modern, high-performance application stack with robust Dependency Injection.
- **MongoDB**: Chosen for its schemaless (polymorphic) nature. A coupon's `details` field has different structure per coupon type; MongoDB handles this natively.
- **Docker & Docker Compose**: Ensures a reproducible build and runtime environment for the app and the DB.
- **SOLID Principles**: The service layer follows the Strategy Pattern to satisfy Open/Closed Principle and make adding new coupon types easy.
- **DTOs & MapStruct**: DTOs are used for API requests/responses. MapStruct provides efficient mapping between DTOs and entities.
- **snake_case JSON Strategy**: The API uses snake_case (e.g., `product_id`). Jackson is configured globally to use SNAKE_CASE so Java code stays camelCase.

## 3. Core Architectural Approach: The Strategy Pattern

The critical requirement was easy extension for new coupon types. Instead of a large `if/else` or `switch`, the project implements the Strategy Pattern:

- `DiscountStrategy` (interface):
  - `getCouponType()` — enum the strategy handles.
  - `isApplicable(cart, coupon)` — checks whether the coupon applies.
  - `calculateDiscount(cart, coupon)` — returns discount value.
  - `applyDiscount(cart, coupon)` — returns updated cart.

- Concrete strategies:
  - `CartWiseStrategy.java`
  - `ProductWiseStrategy.java`
  - `BxGyStrategy.java`

- `DiscountStrategyFactory`: Injected with `List<DiscountStrategy>` by Spring; builds a `Map<CouponType, DiscountStrategy>` and exposes `getStrategy(CouponType)`.

- `DiscountService` (context): Fetches coupons, asks the factory for the appropriate strategy, and defers `isApplicable` / `calculateDiscount` to it.

Benefit: To add a new coupon type, implement a new `DiscountStrategy` and register it as a Spring bean; no changes to `DiscountService` are required.

## 4. How to Run the Project

### Prerequisites

- Java 17 (or higher)
- Apache Maven
- Docker & Docker Compose

### Option 1: Local Development (App in IDE, DB in Docker)

Recommended for development and debugging.

1. Start only the  container: (NOTE: You need to run Docker Desktop first before execute below commands)

```bash
docker-compose up --build
```

### Option 2: Full Docker Mode (App & DB in Docker)

Use this mode to run both the application and MongoDB as containers — a quick way to reproduce a production-like environment locally.

1. Build the images and start the services (foreground):

```bash
docker-compose up --build
```

This command will:

- Build the Spring Boot Docker image (multi-stage Dockerfile).
- Create and start a `mongo-db` container and a `coupon-service-app` container.
- Stream combined logs to your terminal.

2. (Optional) Run in detached mode (background):

```bash
docker-compose up --build -d
```

3. Inspect logs (follow):

```bash
docker-compose logs -f coupon-service-app
```

4. Verify the service is healthy

- Check container status:

```bash
docker-compose ps
```

- Confirm the app is listening and responding (example):

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/actuator/health || true
```

If the above returns `200`, the service is healthy. If you don't have the Actuator endpoint enabled, try a simple endpoint like `/coupons`.

5. Stop and remove containers and volumes (cleanup):

```bash
docker-compose down -v
```

6. Rebuild only the app image (if you changed code) and restart:

```bash
docker-compose build coupon-service-app
docker-compose up -d
```

Notes:

- The application uses the `application-docker.properties` profile when run from the Docker image and connects to MongoDB at `mongo-db:27017`.
- If you need to pass environment variables or override ports, update `docker-compose.yml` or use `docker-compose run -e ...`.
- To remove images to free disk space after `docker-compose down -v`:

```bash
docker image prune -f
```

## 5. API Endpoints

All endpoints are available at http://localhost:8080.

| Method | Endpoint | Description |
|---|---|---|
| POST | /coupons | Creates a new coupon. |
| GET | /coupons | Retrieves all coupons. |
| GET | /coupons/{id} | Retrieves a specific coupon by its ID. |
| PUT | /coupons/{id} | Updates a specific coupon by its ID. |
| DELETE | /coupons/{id} | Deletes a specific coupon by its ID. |
| POST | /applicable-coupons | Checks a cart and returns all applicable coupons and their discount values. |
| POST | /apply-coupon/{id} | Applies one specific coupon to a cart and returns the updated cart. |

For a full list of cURL commands for testing all scenarios, see `api_test_plan.md` (if present).

## 6. Project Documentation (Per Assignment)

### Assumptions Made

- **BxGy Logic:** The specification had ambiguous examples. Decision: BxGy means "Buy X items from a list of `buy` product IDs, get Y items from a list of `get` product IDs, repeated up to a limit." This matches the response logic used.
- **Discount Field:** `discount` for cart-wise and product-wise coupons is interpreted as a percentage (e.g., `10` means 10%).
- **BxGy "Get" Item Choice:** If multiple eligible `get` items are present, the system discounts the cheapest ones first (store-favouring rule).
- **Product IDs:** Treated as `String` (e.g., "P1").

### Implemented Cases

- Full CRUD for `/coupons`.
- Cart-wise: `CartWiseStrategy` calculates percentage discount based on threshold.
- Product-wise: `ProductWiseStrategy` calculates discounts for specific product IDs.
- BxGy: `BxGyStrategy` implements "Buy X from list, Get Y from list" including `repetition_limit`.
- Applicable coupons endpoint iterates coupons and uses strategies to determine applicability and discount.
- Apply coupon endpoint applies a single coupon and returns the updated cart.
- Containerization via Docker Compose.
- Expiry dates on coupons are respected; expired coupons are filtered out.

### Limitations of this Implementation

- Single coupon application: `/apply-coupon/{id}` applies only one coupon. There's no endpoint to apply multiple coupons at once.
- No coupon stacking logic: Can't apply or block stacking; stacking rules are not supported.
- BxGy implementation covers the main use case but may not support more complex AND/OR combinations.
- Basic validation: The API validates shapes and primitive constraints but doesn't deeply validate that `details` matches the `type`. `details` is currently polymorphic.

### Unimplemented Cases & Future Improvements (Brainstorming)

- Coupon stacking & priority: Add `stackable` and `priority` fields and an `applyCoupons(List<String> codes)` flow to validate and apply coupons in order.
- "Best coupon" service: Endpoint to calculate and apply the single coupon that yields the highest discount.
- Coupon usage limits: Add `max_usage_limit` and `max_usage_per_user` fields and a `CouponUsage` collection to track usage.
- More strategy types: `FIXED_AMOUNT`, `FREE_SHIPPING`, `FIRST_TIME_USER`, etc., can be added as separate strategies.
- Advanced scoping: Add fields like `valid_user_ids` or `valid_category_ids` to enable user or category-scoped coupons.

## Notes about validation

- DTO-level validation is present for common fields, but the `details` polymorphic structure is still typed generically in some places. Consider using a discriminated approach (type + typed `details`) for stronger validation.
