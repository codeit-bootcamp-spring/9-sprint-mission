CREATE TABLE IF NOT EXISTS binary_contents (
                                               id           uuid PRIMARY KEY,
                                               created_at   timestamp with time zone NOT NULL,
                                               file_name    varchar(255)             NOT NULL,
    size         bigint                   NOT NULL,
    content_type varchar(100)             NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
                                     id         uuid PRIMARY KEY,
                                     created_at timestamp with time zone NOT NULL,
                                     updated_at timestamp with time zone,
                                     username   varchar(50) UNIQUE       NOT NULL,
    email      varchar(100) UNIQUE      NOT NULL,
    password   varchar(60)              NOT NULL,
    profile_id UUID UNIQUE,
    role varchar(20) NOT NULL,
    CONSTRAINT fk_user_binary_content FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
    );


CREATE TABLE IF NOT EXISTS channels (
                                        id          uuid PRIMARY KEY,
                                        created_at  timestamp with time zone NOT NULL,
                                        updated_at  timestamp with time zone,
                                        name        varchar(100),
    description varchar(500),
    type        varchar(100)             NOT NULL,
    CONSTRAINT check_channel_type CHECK (type in ('PUBLIC', 'PRIVATE'))
    );

CREATE TABLE IF NOT EXISTS messages (
                                        id         uuid PRIMARY KEY,
                                        created_at timestamp with time zone NOT NULL,
                                        updated_at timestamp with time zone,
                                        content    text,
                                        channel_id uuid                     NOT NULL,
                                        author_id  uuid,
                                        CONSTRAINT fk_message_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_user_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS read_statuses (
                                             id           uuid PRIMARY KEY,
                                             created_at   timestamp with time zone NOT NULL,
                                             updated_at   timestamp with time zone,
                                             user_id      uuid                     NOT NULL,
                                             channel_id   uuid                     NOT NULL,
                                             last_read_at timestamp with time zone NOT NULL,
                                             CONSTRAINT uk_read_status UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS message_attachments (
                                                   message_id    uuid NOT NULL,
                                                   attachment_id uuid NOT NULL,
                                                   PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_attachment_message_id FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_binary_id FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
    );





