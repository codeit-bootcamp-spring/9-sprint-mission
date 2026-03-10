-- [주의] 기존 테이블들을 관계 역순으로 삭제 (초기화용) [cite: 2026-03-05]
DROP TABLE IF EXISTS channel_participants CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- 1. BinaryContent
CREATE TABLE binary_contents (
                                 id           uuid PRIMARY KEY,
                                 created_at   timestamptz  NOT NULL,
                                 updated_at   timestamptz  NOT NULL,
                                 file_name    varchar(255) NOT NULL,
                                 size         bigint       NOT NULL,
                                 content_type varchar(100) NOT NULL
);

-- 2. Users
CREATE TABLE users (
                       id         uuid PRIMARY KEY,
                       created_at timestamptz         NOT NULL,
                       updated_at timestamptz         NOT NULL,
                       username   varchar(50) UNIQUE  NOT NULL,
                       email      varchar(100) UNIQUE NOT NULL,
                       password   varchar(60)         NOT NULL,
                       profile_id uuid,
                       CONSTRAINT fk_user_profile FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- 3. Channels
CREATE TABLE channels (
                          id              uuid PRIMARY KEY,
                          created_at      timestamptz NOT NULL,
                          updated_at      timestamptz NOT NULL,
                          name            varchar(100),
                          type            varchar(20) NOT NULL,
                          description     text,
                          last_message_at timestamptz
);

-- 4. Messages
CREATE TABLE messages (
                          id         uuid PRIMARY KEY,
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz NOT NULL,
                          content    text        NOT NULL,
                          author_id  uuid        NOT NULL,
                          channel_id uuid        NOT NULL,
                          CONSTRAINT fk_message_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
                          CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

-- 5. ReadStatuses
CREATE TABLE read_statuses (
                               id           uuid PRIMARY KEY,
                               created_at   timestamptz NOT NULL,
                               updated_at   timestamptz NOT NULL,
                               user_id      uuid        NOT NULL,
                               channel_id   uuid        NOT NULL,
                               last_read_at timestamptz NOT NULL,
                               CONSTRAINT fk_read_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                               CONSTRAINT fk_read_status_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

-- 6. UserStatuses (ERROR 해결: status 컬럼 추가) [cite: 2026-03-05]
CREATE TABLE user_statuses (
                               id             uuid PRIMARY KEY,
                               created_at     timestamptz NOT NULL,
                               updated_at     timestamptz NOT NULL,
                               user_id        uuid UNIQUE NOT NULL,
                               status         varchar(20) NOT NULL, -- 엔티티의 status 필드와 매핑됨 [cite: 2026-03-05]
                               last_active_at timestamptz,
                               CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 7. Message_Attachments
CREATE TABLE message_attachments (
                                     message_id uuid NOT NULL,
                                     binary_id  uuid NOT NULL,
                                     PRIMARY KEY (message_id, binary_id),
                                     CONSTRAINT fk_attachment_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
                                     CONSTRAINT fk_attachment_binary FOREIGN KEY (binary_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

-- 8. Channel_Participants (다대다 관계용 테이블 추가) [cite: 2026-03-05]
CREATE TABLE channel_participants (
                                      channel_id uuid NOT NULL,
                                      user_id    uuid NOT NULL,
                                      PRIMARY KEY (channel_id, user_id),
                                      CONSTRAINT fk_participant_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
                                      CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);