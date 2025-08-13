-- H2-compatible reset script for integration tests without containers
-- Delete in reverse order of foreign key dependencies
DELETE FROM water_intakes;
DELETE FROM alarm_settings;
DELETE FROM users;

-- Reset auto-increment sequences to start from 1
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE alarm_settings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE water_intakes ALTER COLUMN id RESTART WITH 1;