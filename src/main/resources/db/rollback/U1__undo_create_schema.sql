-- Rollback script for V1__create_schema.sql
-- This file is documentation only. It is NOT executed automatically by Flyway.
-- To rollback, create a new versioned migration (e.g. V2__drop_all_tables.sql)
-- with the contents below, or execute manually against the database.

DROP INDEX IF EXISTS idx_water_intakes_user_datetime;
DROP TABLE IF EXISTS water_intakes;
DROP TABLE IF EXISTS alarm_settings;
DROP INDEX IF EXISTS idx_user_public_id;
DROP TABLE IF EXISTS users;
