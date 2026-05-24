-- DROP TABLE IF EXISTS binary_contents CASCADE;
-- DROP TABLE IF EXISTS channels CASCADE;
-- DROP TABLE IF EXISTS messages CASCADE;
-- DROP TABLE IF EXISTS message_attachment CASCADE;
-- DROP TABLE IF EXISTS read_statuses CASCADE;
-- DROP TABLE IF EXISTS user_statuses CASCADE;
-- DROP TABLE IF EXISTS users CASCADE;

-- BinaryContent
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    updated_at   timestamptz,
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL
--    bytes        bytea        NOT NULL
);

-- Channel
CREATE TABLE channels
(
    id               uuid PRIMARY KEY,
    created_at       timestamptz NOT NULL,
    updated_at       timestamptz,
    name             varchar(100),
    description      varchar(150),
    type             varchar(10) NOT NULL CHECK ( type in('PUBLIC', 'PRIVATE') )
);

-- User
CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamptz         NOT NULL,
    updated_at timestamptz,
    username   varchar(50) UNIQUE  NOT NULL,
    email      varchar(100) UNIQUE NOT NULL,
    password   varchar(60)         NOT NULL,
    profile_id uuid,

        CONSTRAINT fk_user_binary_content
        FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- UserStatus
CREATE TABLE user_statuses
(
    id               uuid PRIMARY KEY,
    created_at       timestamptz NOT NULL,
    updated_at       timestamptz,
    user_id          uuid NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    last_active_at   timestamptz NOT NULL
);

-- Message
CREATE TABLE messages
(
    id               uuid PRIMARY KEY,
    created_at       timestamptz NOT NULL,
    updated_at       timestamptz,
    content          text,
    channel_id       uuid NOT NULL ,
    author_id        uuid,
    CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_user
        FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Message-Attachment
CREATE TABLE message_attachment
(
    message_id       uuid NOT NULL,
    attachment_id    uuid NOT NULL,

    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_attachment_message
        FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_attachment_binary
        FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);


-- ReadStatus
CREATE TABLE read_statuses
(
    id            uuid PRIMARY KEY,
    created_at    timestamptz NOT NULL,
    updated_at    timestamptz,
    user_id       uuid NOT NULL,
    channel_id    uuid NOT NULL,
    last_read_at  timestamptz NOT NULL,

    CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT uk_read_status_user_channel
        UNIQUE (user_id, channel_id)
);




