DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_binary_contents_status'
    ) THEN
        ALTER TABLE binary_contents
            ADD CONSTRAINT chk_binary_contents_status CHECK (status IN ('PROCESSING', 'SUCCESS', 'FAIL'));
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_channels_type'
    ) THEN
        ALTER TABLE channels
            ADD CONSTRAINT chk_channels_type CHECK (type IN ('PUBLIC', 'PRIVATE'));
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_users_role'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'CHANNEL_MANAGER', 'USER'));
    END IF;
END $$;
