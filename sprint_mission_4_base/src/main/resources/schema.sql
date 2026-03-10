CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID         NOT NULL PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    file_name    VARCHAR(255),
    size         BIGINT,
    content_type VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS users
(
    id         UUID        NOT NULL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(60) NOT NULL,
    profile_id UUID REFERENCES binary_contents (id) ON DELETE SET NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID        NOT NULL PRIMARY KEY,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ,
    user_id        UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    last_active_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_user_statuses_user_id UNIQUE (user_id)
    );

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID        NOT NULL PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
    );

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID        NOT NULL PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
    );

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID        NOT NULL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    content    TEXT,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  UUID REFERENCES users (id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
    );