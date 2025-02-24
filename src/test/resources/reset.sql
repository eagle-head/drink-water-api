TRUNCATE TABLE water_intakes RESTART IDENTITY CASCADE;
TRUNCATE TABLE alarm_settings RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

SELECT setval('users_id_seq', 1, false);
SELECT setval('alarm_settings_id_seq', 1, false);
SELECT setval('water_intakes_id_seq', 1, false);
