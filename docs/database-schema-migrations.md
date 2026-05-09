# Database Schema Migrations

Production runs with `spring.jpa.hibernate.ddl-auto=validate`, so entity metadata is used only to validate the database shape. Schema changes must be applied to the database before deploying an application version that depends on them.

## Profile Policy

| Profile | `ddl-auto` | Purpose |
| --- | --- | --- |
| common/default | `validate` | Prevent accidental schema mutation unless a profile explicitly opts in. |
| local | `update` | Keep local Docker Compose databases convenient during development. |
| test | `update` | Let Testcontainers/MySQL create and update the disposable test schema. |
| prod | `validate` | Require SQL migrations before deploying code that depends on schema changes. |

## Payment Reservation Uniqueness

The payment flow is idempotent per reservation. The database must enforce that rule with a unique constraint on `PAYMENTS.reservation_id`.

Apply:

```sql
SOURCE docs/sql/20260507_ensure_payment_reservation_unique.sql;
```

Pre-deploy checks:

```sql
SELECT reservation_id, COUNT(*) AS payment_count
FROM PAYMENTS
WHERE reservation_id IS NOT NULL
GROUP BY reservation_id
HAVING COUNT(*) > 1;
```

Expected result: no rows. If rows are returned, resolve duplicate payment history before applying the constraint.

Post-deploy checks:

```sql
SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'PAYMENTS'
  AND CONSTRAINT_NAME = 'uk_payment_reservation';
```

Expected result: one `UNIQUE` constraint named `uk_payment_reservation`.

## Migration Policy

- Keep production `ddl-auto=validate`.
- Apply SQL migrations before deploying code that relies on the new schema.
- Add a rollback note for every migration that changes constraints or indexes.
- Prefer a dedicated migration tool such as Flyway once a full baseline migration is ready.

Rollback for `uk_payment_reservation`:

```sql
ALTER TABLE PAYMENTS DROP INDEX uk_payment_reservation;
CREATE INDEX idx_reservation_id ON PAYMENTS (reservation_id);
```
