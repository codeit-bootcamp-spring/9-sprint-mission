CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

-- Create tables
CREATE TABLE binary_contents
(
    id           UUID                         NOT NULL,
    created_at   TIMESTAMPTZ                  NOT NULL,
    file_name    VARCHAR(255)                 NOT NULL,
    size         BIGINT                       NOT NULL,
    content_type VARCHAR(100)                 NOT NULL,
    CONSTRAINT binary_contents_pkey PRIMARY KEY (id)
);

CREATE TABLE channels
(
    id          UUID                         NOT NULL,
    created_at  TIMESTAMPTZ                  NOT NULL,
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100)                 NOT NULL,
    description VARCHAR(500),
    type        channel_type                 NOT NULL,
    CONSTRAINT channels_pkey PRIMARY KEY (id)
);

CREATE TABLE messages
(
    id         UUID                         NOT NULL,
    created_at TIMESTAMPTZ                  NOT NULL,
    updated_at TIMESTAMPTZ,
    content    TEXT,
    channel_id UUID                         NOT NULL,
    author_id  UUID,
    CONSTRAINT messages_pkey PRIMARY KEY (id)
);

CREATE TABLE message_attachments
(
    message_id    UUID                         NOT NULL,
    attachment_id UUID                         NOT NULL,
    CONSTRAINT message_attachments_pkey PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE read_statuses
(
    id           UUID                         NOT NULL,
    created_at   TIMESTAMPTZ                  NOT NULL,
    updated_at   TIMESTAMPTZ,
    user_id      UUID                         NOT NULL,
    channel_id   UUID                         NOT NULL,
    last_read_at TIMESTAMPTZ                  NOT NULL,
    CONSTRAINT read_statuses_pkey PRIMARY KEY (id)
);

CREATE TABLE user_statuses
(
    id             UUID                         NOT NULL,
    created_at     TIMESTAMPTZ                  NOT NULL,
    updated_at     TIMESTAMPTZ,
    user_id        UUID                         NOT NULL,
    last_active_at TIMESTAMPTZ                  NOT NULL,
    CONSTRAINT user_statuses_pkey PRIMARY KEY (id)
);

CREATE TABLE users
(
    id         UUID                         NOT NULL,
    created_at TIMESTAMPTZ                  NOT NULL,
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50)                  NOT NULL,
    email      VARCHAR(100)                 NOT NULL,
    password   VARCHAR(60)                  NOT NULL,
    profile_id UUID,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- Constraints and Indexes
ALTER TABLE read_statuses
    ADD CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id);

ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uk_users_username UNIQUE (username);

ALTER TABLE users
    ADD CONSTRAINT uk_users_profile_id UNIQUE (profile_id);

ALTER TABLE user_statuses
    ADD CONSTRAINT user_statuses_user_id_key UNIQUE (user_id);

CREATE INDEX idx_channels_name ON channels (name);

-- Foreign Keys
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL;

CREATE INDEX idx_messages_author_id ON messages (author_id);

ALTER TABLE messages
    ADD CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE;

CREATE INDEX idx_messages_channel_id ON messages (channel_id);

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachment_message
        FOREIGN KEY (message_id)
            REFERENCES messages (id)
            ON DELETE CASCADE;

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachment_binary
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE;

CREATE INDEX idx_message_attachments_attachment_id
    ON message_attachments (attachment_id);

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_statuses_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE;

CREATE INDEX idx_read_statuses_channel_id ON read_statuses (channel_id);

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_statuses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX idx_read_statuses_user_id ON read_statuses (user_id);

ALTER TABLE users
    ADD CONSTRAINT fk_user_binary_content FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL;

ALTER TABLE user_statuses
    ADD CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
