DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- BinaryContent
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL
--     ,bytes        bytea        NOT NULL
);

-- users
CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamptz         NOT NULL,
    updated_at timestamptz,
    username   varchar(50) UNIQUE  NOT NULL,
    email      varchar(100) UNIQUE NOT NULL,
    password   varchar(60)         NOT NULL,
    profile_id uuid
);

-- 제약 조건
-- User (1) -> BinaryContent (1)
ALTER TABLE users
    ADD CONSTRAINT fk_user_binary_content
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL;

CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        uuid UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL
);

-- UserStatus (1) -> User (1)
ALTER TABLE user_statuses
    ADD CONSTRAINT fk_user_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;
