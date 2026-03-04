-- UUID 생성을 위해 (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--------------------------------------------------
-- binary_contents
--------------------------------------------------
CREATE TABLE binary_contents (
                                 id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 created_at timestamptz NOT NULL,
                                 file_name varchar(255) NOT NULL,
                                 size bigint NOT NULL,
                                 content_type varchar(100) NOT NULL,
                                 bytes bytea NOT NULL
);

--------------------------------------------------
-- users
--------------------------------------------------
CREATE TABLE users (
                       id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                       created_at timestamptz NOT NULL,
                       updated_at timestamptz,
                       username varchar(50) NOT NULL UNIQUE,
                       email varchar(100) NOT NULL UNIQUE,
                       password varchar(60) NOT NULL,
                       profile_id uuid,
                       CONSTRAINT fk_users_profile
                           FOREIGN KEY (profile_id)
                               REFERENCES binary_contents(id)
                               ON DELETE SET NULL
);

--------------------------------------------------
-- user_statuses
--------------------------------------------------
CREATE TABLE user_statuses (
                               id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                               created_at timestamptz NOT NULL,
                               updated_at timestamptz,
                               user_id uuid NOT NULL UNIQUE,
                               last_active_at timestamptz NOT NULL,
                               CONSTRAINT fk_user_status_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE
);

--------------------------------------------------
-- channels
--------------------------------------------------
CREATE TABLE channels (
                          id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz,
                          name varchar(100),
                          description varchar(500),
                          type varchar(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

--------------------------------------------------
-- messages
--------------------------------------------------
CREATE TABLE messages (
                          id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz,
                          content text,
                          channel_id uuid NOT NULL,
                          author_id uuid,
                          CONSTRAINT fk_message_channel
                              FOREIGN KEY (channel_id)
                                  REFERENCES channels(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_message_author
                              FOREIGN KEY (author_id)
                                  REFERENCES users(id)
                                  ON DELETE SET NULL
);

--------------------------------------------------
-- read_statuses
--------------------------------------------------
CREATE TABLE read_statuses (
                               id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                               created_at timestamptz NOT NULL,
                               updated_at timestamptz,
                               user_id uuid NOT NULL,
                               channel_id uuid NOT NULL,
                               last_read_at timestamptz NOT NULL,
                               CONSTRAINT fk_read_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_read_channel
                                   FOREIGN KEY (channel_id)
                                       REFERENCES channels(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT uk_read UNIQUE (user_id, channel_id)
);

--------------------------------------------------
-- message_attachments
--------------------------------------------------
CREATE TABLE message_attachments (
                                     message_id uuid NOT NULL,
                                     attachment_id uuid NOT NULL,
                                     PRIMARY KEY (message_id, attachment_id),
                                     CONSTRAINT fk_attachment_message
                                         FOREIGN KEY (message_id)
                                             REFERENCES messages(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_attachment_binary
                                         FOREIGN KEY (attachment_id)
                                             REFERENCES binary_contents(id)
                                             ON DELETE CASCADE
);