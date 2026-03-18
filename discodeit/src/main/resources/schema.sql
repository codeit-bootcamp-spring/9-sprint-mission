-- 기존 테이블이 있다면 삭제 (의존성 역순으로 삭제해야 안전함)
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- 1. binary_contents (의존성 없음)
CREATE TABLE binary_contents (
                                 id uuid PRIMARY KEY,
                                 created_at timestamptz NOT NULL,
                                 file_name varchar(255) NOT NULL,
                                 size bigint NOT NULL,
                                 content_type varchar(100) NOT NULL
    -- 과제 요구사항: bytes 속성은 메타 정보 분리를 위해 제외
);

-- 2. users (binary_contents 참조)
CREATE TABLE users (
                       id uuid PRIMARY KEY,
                       created_at timestamptz NOT NULL,
                       updated_at timestamptz,
                       username varchar(50) UNIQUE NOT NULL,
                       email varchar(100) UNIQUE NOT NULL,
                       password varchar(60) NOT NULL,
                       profile_id uuid UNIQUE, -- ERD 요구사항: UK 추가
                       CONSTRAINT fk_user_binary_content FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- 3. channels (의존성 없음)
CREATE TABLE channels (
                          id uuid PRIMARY KEY,
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz,
                          name varchar(100),
                          description varchar(500),
                          type varchar(10) NOT NULL -- ENUM(PUBLIC, PRIVATE)
);

-- 4. user_statuses (users 참조)
CREATE TABLE user_statuses (
                               id uuid PRIMARY KEY,
                               created_at timestamptz NOT NULL,
                               updated_at timestamptz,
                               user_id uuid UNIQUE NOT NULL, -- ERD 요구사항: FK, UK, NN
                               last_active_at timestamptz NOT NULL,
                               CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 5. messages (channels, users 참조)
CREATE TABLE messages (
                          id uuid PRIMARY KEY,
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz,
                          content text,
                          channel_id uuid NOT NULL, -- FK, NN
                          author_id uuid, -- FK
                          CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
                          CONSTRAINT fk_message_user FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- 6. read_statuses (users, channels 참조)
CREATE TABLE read_statuses (
                               id uuid PRIMARY KEY,
                               created_at timestamptz NOT NULL,
                               updated_at timestamptz,
                               user_id uuid NOT NULL, -- FK, NN
                               channel_id uuid NOT NULL, -- FK, NN
                               last_read_at timestamptz NOT NULL,
                               CONSTRAINT uk_read_status_user_channel UNIQUE (user_id, channel_id), -- ERD 요구사항: 복합 UK
                               CONSTRAINT fk_read_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                               CONSTRAINT fk_read_status_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

-- 7. message_attachments (messages, binary_contents 참조)
CREATE TABLE message_attachments (
                                     message_id uuid NOT NULL,
                                     attachment_id uuid NOT NULL,
                                     PRIMARY KEY (message_id, attachment_id),
                                     CONSTRAINT fk_attachment_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
                                     CONSTRAINT fk_attachment_binary_content FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);