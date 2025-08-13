-- H2-compatible data for integration tests without containers
-- Insert users with specific IDs
INSERT INTO users (id, public_id, email, first_name, last_name, birth_date, biological_sex, weight, weight_unit, height, height_unit)
VALUES
    (1, 'fbc58717-5d48-4041-9f1c-257e8052428f', 'john.doe@test.com', 'John', 'Doe', '1990-01-01', 1, 70.5, 1, 175, 1),
    (2, '571cff71-0db3-445e-9a9a-b542f3125a95', 'jane.smith@test.com', 'Jane', 'Smith', '1985-05-15', 2, 60.0, 1, 165, 1),
    (3, 'ac2539e8-03e8-4daf-a456-70ca641ec19d', 'alex.jones@test.com', 'Alex', 'Jones', '1992-10-10', 1, 80.0, 1, 180, 1);

-- Insert alarm settings for each user
INSERT INTO alarm_settings (goal, interval_minutes, daily_start_time, daily_end_time, user_id)
VALUES
    (2000, 60, '08:00:00', '22:00:00', 1),
    (1500, 45, '07:00:00', '21:00:00', 2),
    (1750, 30, '09:00:00', '20:00:00', 3);

-- Insert water intakes for each user
INSERT INTO water_intakes (date_time_utc, volume, volume_unit, user_id)
VALUES
    -- User 1
    ('2024-08-14T10:00:00Z', 250, 1, 1),
    ('2024-08-14T11:00:00Z', 300, 1, 1),
    ('2024-08-14T12:00:00Z', 200, 1, 1),
    ('2024-08-14T13:00:00Z', 400, 1, 1),
    ('2024-08-14T14:00:00Z', 500, 1, 1),

    -- User 2
    ('2024-08-14T10:30:00Z', 300, 1, 2),
    ('2024-08-14T11:30:00Z', 350, 1, 2),
    ('2024-08-14T12:30:00Z', 250, 1, 2),
    ('2024-08-14T13:30:00Z', 450, 1, 2),
    ('2024-08-14T14:30:00Z', 500, 1, 2),

    -- User 3
    ('2024-08-14T09:30:00Z', 500, 1, 3),
    ('2024-08-14T10:30:00Z', 250, 1, 3),
    ('2024-08-14T11:30:00Z', 350, 1, 3),
    ('2024-08-14T12:30:00Z', 450, 1, 3),
    ('2024-08-14T13:30:00Z', 550, 1, 3);

-- H2 uses ALTER TABLE to set auto-increment restart values instead of setval
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;
ALTER TABLE alarm_settings ALTER COLUMN id RESTART WITH 4;
ALTER TABLE water_intakes ALTER COLUMN id RESTART WITH 16;