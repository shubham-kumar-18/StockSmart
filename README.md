# StockSmart

StockSmart is a Java 21, Spring Boot, Thymeleaf and MySQL retail inventory application. The UI is server-rendered; there is no separate JavaScript frontend application.

## Current implementation

Phase 1 includes MySQL persistence and Flyway migration, organization creation, public account registration, BCrypt password hashing, and database-backed Spring Security form login/logout. Phase 2 adds workspace-scoped product/category and location management, search/filter controls, and database-derived dashboard counts. Phase 3 adds per-product/per-location inventory, opening stock, receipts, positive/negative adjustments, transfers, and recent movement history. Phase 4 adds order drafts, price snapshots, stock reservation on confirmation, reservation release on eligible cancellation, and stock deduction on fulfillment. Phase 5 adds suppliers, multi-item purchase orders, partial goods receipts, purchase history and stock receipt integration. Phase 6 adds barcode lookup, inventory valuation, fulfilled product rankings, dated movement reports and CSV export. Stock-changing operations lock inventory rows and update balances plus the movement ledger in one transaction. A public signup creates a new, isolated workspace and its first administrator; there is no public join-existing-workspace flow. Invite-based additional users can be added later.

## Folder structure

```text
src/main/java/com/stocksmart/
  auth/           registration form, controller and transactional service
  barcode/        workspace-scoped keyboard-wedge barcode lookup
  category/       workspace scoped product categories
  config/         security configuration
  dashboard/      authenticated landing page
  inventory/      location-level stock and transactional movement ledger
  location/       workspace scoped store/warehouse CRUD
  organization/   organization entity and repository
  order/          order creation, pricing snapshots, reservation and fulfillment workflow
  product/        workspace scoped product catalog CRUD
  purchasing/     purchase orders and transactional goods receipts
  report/         database-derived reports and CSV export
  supplier/       supplier management and purchase history
  security/       database-backed UserDetailsService
  user/           user entity and repository
  StockSmartApplication.java
src/main/resources/
  db/migration/   Flyway SQL migrations
  static/css/     responsive styles
  templates/      Thymeleaf pages
  application.yml
```

As modules arrive, place their controllers, DTOs, services, repositories and entities in their own feature packages (for example `product/`, `inventory/`, `location/`), keeping business rules and transactions in services.

## Requirements

- Java 21
- Maven 3.6.3+
- MySQL 8

Spring Boot 3.5.16 is pinned for this Java 21 application. Configuration uses standard Spring Boot starters, JPA/Hibernate, Spring Security and Flyway.

## Run locally

1. Create a MySQL database and a local user, for example:

   ```sql
   CREATE DATABASE stocksmart CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   CREATE USER 'stocksmart'@'localhost' IDENTIFIED BY 'choose-a-local-password';
   GRANT ALL PRIVILEGES ON stocksmart.* TO 'stocksmart'@'localhost';
   ```

2. Set `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` in your shell, or copy `.env.example` values into your IDE run configuration (Spring does not automatically load `.env`). Never commit real credentials.
3. Start with `mvn spring-boot:run`. Flyway creates the organization and user tables. Hibernate validates the schema; it does not create or mutate tables.
4. Visit `http://localhost:8080/register`, create a workspace, then sign in at `/login`.

No default admin or demo business records are seeded. If startup fails, verify MySQL is running, the database exists, and the credentials are set.

## Data isolation

The initial schema connects each user to one organization. Public registration creates both in one transaction, and the application never accepts an organization ID from registration input. Feature services must derive the organization from the authenticated user and scope each query by that organization before returning or mutating a record. Keep this rule when adding all business tables.

## Planned phases

1. Authentication and organization foundation (implemented)
2. Product/category and location CRUD, real dashboard product/location counts (implemented)
3. Location inventory, receipts, adjustments, transfers and movement history (implemented)
4. Orders and transactional stock reservation/fulfillment policy (implemented)
5. Suppliers, purchase orders and partial receipts (implemented)
6. Barcode lookup, database-backed reports and CSV export (implemented)
7. Integration tests, responsive refinements, date filters and workflow documentation (remaining)

## Inventory and order rules

- Inventory is stored per product/location. On-hand, reserved and available (`on-hand - reserved`) are tracked separately.
- Transfers and stock removals cannot use quantities already reserved by confirmed orders.
- Opening stock is allowed once per product/location. Receipts, adjustments and transfers create ledger entries; receipts and transfers require unique references per product to block duplicate reprocessing.
- Orders are drafts until confirmed. Confirmation reserves stock; cancellation of a confirmed order releases it; fulfillment deducts stock and its reservation exactly once. Fulfilled orders cannot be cancelled through the standard flow.
- Purchase orders snapshot product name, SKU, quantity and unit cost. Only a recorded receipt adds stock. Receipt reference, purchase receipt records, received quantity and stock movement are persisted in one transaction; over-receipt is rejected.
- Inventory valuation is on-hand quantity multiplied by the current product cost price. Records without a cost price are omitted from valuation. Fast-moving rankings include fulfilled orders only; with no completed sales, the report says there is insufficient history.
- Barcode lookup supports keyboard-wedge scanners that type the code and submit. No RFID reader integration is claimed.

## Initial routes

| Method | Route | Access | Purpose |
|---|---|---|---|
| GET | `/register` | Public | Registration form |
| POST | `/register` | Public | Create isolated workspace and admin |
| GET/POST | `/login` | Public | Spring Security form login |
| POST | `/logout` | Authenticated | End session (CSRF protected) |
| GET | `/dashboard` | Authenticated | Workspace landing page |
| GET/POST | `/products`, `/products/new` | Authenticated | Product catalog |
| GET/POST | `/locations`, `/locations/new` | Authenticated | Warehouse/store setup |
| GET/POST | `/inventory` and `/inventory/*` | Authenticated | Stock counts and movements |
| GET/POST | `/orders` | Authenticated | Draft, confirm, cancel and fulfill orders |
| GET/POST | `/suppliers` | Authenticated | Supplier records |
| GET/POST | `/purchase-orders` | Authenticated | Purchasing and goods receipt |
| GET | `/barcode?code=...` | Authenticated | Barcode lookup |
| GET | `/reports`, `/reports/inventory.csv` | Authenticated | Database-backed reports and CSV |

CSRF protection remains enabled for all web forms. Passwords are encoded with BCrypt. Add migrations for future schema changes; do not use `ddl-auto: update`.
