-- DROP TABLE IF EXISTS binary_contents CASCADE;
-- DROP TABLE IF EXISTS channels CASCADE;
-- DROP TABLE IF EXISTS messages CASCADE;
-- DROP TABLE IF EXISTS message_attachment CASCADE;
-- DROP TABLE IF EXISTS read_statuses CASCADE;
-- DROP TABLE IF EXISTS users CASCADE;
-- DROP TABLE IF EXISTS notifications CASCADE;

-- BinaryContent
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    updated_at   timestamptz,
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL,
    status       varchar(20)  NOT NULL DEFAULT 'PROCESSING'
);

-- Channel
CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(150),
    type        varchar(10) NOT NULL CHECK ( type in ('PUBLIC', 'PRIVATE') )
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
    role       varchar(20)         NOT NULL DEFAULT 'USER',

    CONSTRAINT fk_user_binary_content
        FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- Message
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    text,
    channel_id uuid NOT NULL,
    author_id  uuid,
    CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_user
        FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Message-Attachment
CREATE TABLE message_attachment
(
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,

    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_attachment_message
        FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_attachment_binary
        FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

-- ReadStatus
CREATE TABLE read_statuses
(
    id                   uuid PRIMARY KEY,
    created_at           timestamptz NOT NULL,
    updated_at           timestamptz,
    user_id              uuid NOT NULL,
    channel_id           uuid NOT NULL,
    last_read_at         timestamptz NOT NULL,
    notification_enabled boolean     NOT NULL DEFAULT false,

    CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uk_read_status_user_channel
        UNIQUE (user_id, channel_id)
);

-- Notification
CREATE TABLE notifications
(
    id          uuid PRIMARY KEY,
    created_at  timestamptz  NOT NULL,
    updated_at  timestamptz,
    receiver_id uuid         NOT NULL,
    title       varchar(255) NOT NULL,
    content     text,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE DATABASE discodeit_prod WITH OWNER = discodeit_user;
