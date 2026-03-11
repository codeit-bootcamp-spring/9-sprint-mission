-- 1. binary_contents 테이블 (users와 message_attachments에서 참조하므로 가장 먼저 생성)
CREATE TABLE binary_contents (
                                 id UUID PRIMARY KEY,
                                 created_at TIMESTAMPTZ NOT NULL,
                                 file_name VARCHAR(255) NOT NULL,
                                 size BIGINT NOT NULL,
                                 content_type VARCHAR(100) NOT NULL,
                                 bytes BYTEA NOT NULL
);

-- 2. users 테이블 (profile_id가 binary_contents를 참조)
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       profile_id UUID UNIQUE,
                       CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- 3. channels 테이블
CREATE TABLE channels (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          name VARCHAR(100),
                          description VARCHAR(500),
    -- ERD에 명시된 varchar(10)과 ENUM 조건을 CHECK 제약조건으로 정확히 반영
                          type VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

-- 4. user_statuses 테이블 (user_id가 users를 참조)
CREATE TABLE user_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL UNIQUE,
                               last_active_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_user_statuses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. read_statuses 테이블 (user_id는 users를, channel_id는 channels를 참조)
CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL,
                               channel_id UUID NOT NULL,
                               last_read_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_read_statuses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT fk_read_statuses_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
                               CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

-- 6. messages 테이블 (channel_id는 channels를, author_id는 users를 참조)
CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          content TEXT,
                          channel_id UUID NOT NULL,
                          author_id UUID,
                          CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. message_attachments 테이블 (messages와 binary_contents를 참조하는 다대다 연결 테이블)
CREATE TABLE message_attachments (
                                     message_id UUID NOT NULL,
                                     attachment_id UUID NOT NULL,
                                     PRIMARY KEY (message_id, attachment_id),
                                     CONSTRAINT fk_message_attachments_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_message_attachments_attachment FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);
