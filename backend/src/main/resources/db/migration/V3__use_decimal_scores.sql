ALTER TABLE submissions
    ALTER COLUMN score TYPE NUMERIC(10, 2)
        USING score::numeric;