-- Ensures one payment row per reservation.
-- Run this before starting the application with spring.jpa.hibernate.ddl-auto=validate.
-- MySQL DDL commits implicitly, so run it during a planned maintenance window.

DELIMITER //

CREATE PROCEDURE apply_payment_reservation_unique()
BEGIN
    IF EXISTS (
        SELECT 1
        FROM (
            SELECT reservation_id
            FROM PAYMENTS
            WHERE reservation_id IS NOT NULL
            GROUP BY reservation_id
            HAVING COUNT(*) > 1
        ) duplicate_payments
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot add uk_payment_reservation: duplicate PAYMENTS.reservation_id values exist.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'PAYMENTS'
          AND CONSTRAINT_NAME = 'uk_payment_reservation'
          AND CONSTRAINT_TYPE = 'UNIQUE'
    ) THEN
        ALTER TABLE PAYMENTS
            ADD CONSTRAINT uk_payment_reservation UNIQUE (reservation_id);
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'PAYMENTS'
          AND INDEX_NAME = 'idx_reservation_id'
    ) THEN
        ALTER TABLE PAYMENTS DROP INDEX idx_reservation_id;
    END IF;
END//

DELIMITER ;

CALL apply_payment_reservation_unique();
DROP PROCEDURE apply_payment_reservation_unique;
