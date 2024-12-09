-- Insert users with specific IDs
INSERT INTO users (id, public_id, email, first_name, last_name, birth_date, biological_sex, weight, weight_unit, height, height_unit, created_at, updated_at)
VALUES
    (1, 'f4913d86-56e7-4129-b084-102fc7e2cf38', 'john.doe@test.com', 'John', 'Doe', '1990-01-01T00:00:00+00:00', 1, 70.5, 1, 175, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'bb590732-97c6-4471-ae03-612235d8db56', 'jane.smith@test.com', 'Jane', 'Smith', '1985-05-15T00:00:00+00:00', 2, 60.0, 1, 165, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '4eb296af-9307-4a7c-b319-aaca98b43466', 'alex.jones@test.com', 'Alex', 'Jones', '1992-10-10T00:00:00+00:00', 1, 80.0, 1, 180, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert alarm settings for each user
INSERT INTO alarm_settings (interval_minutes, start_time, end_time, user_id, created_at, updated_at)
VALUES
    (60, '2024-01-01T08:00:00+00:00', '2024-01-01T22:00:00+00:00', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (45, '2024-01-01T07:00:00+00:00', '2024-01-01T21:00:00+00:00', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (30, '2024-01-01T09:00:00+00:00', '2024-01-01T20:00:00+00:00', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert water intakes for each user
INSERT INTO water_intakes (date_time_utc, volume, volume_unit, user_id, created_at, updated_at)
VALUES
    -- User 1
    ('2024-08-14T10:00:00+00:00', 250, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T11:00:00+00:00', 300, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T12:00:00+00:00', 200, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T13:00:00+00:00', 400, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T14:00:00+00:00', 500, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- User 2
    ('2024-08-14T10:30:00+00:00', 300, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T11:30:00+00:00', 350, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T12:30:00+00:00', 250, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T13:30:00+00:00', 450, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T14:30:00+00:00', 500, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- User 3
    ('2024-08-14T09:30:00+00:00', 500, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T10:30:00+00:00', 250, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T11:30:00+00:00', 350, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T12:30:00+00:00', 450, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2024-08-14T13:30:00+00:00', 550, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update the sequence for the users table
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));

-- Update the sequence for the alarm_settings table
SELECT setval('alarm_settings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM alarm_settings));

-- Update the sequence for the water_intakes table
SELECT setval('water_intakes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM water_intakes));
