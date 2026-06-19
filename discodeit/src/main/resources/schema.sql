-- 1. 이진 컨텐츠 (프로필 사진 등)
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    status       VARCHAR(20)  NOT NULL
    );

-- 2. 유저
CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    profile_id UUID         REFERENCES binary_contents (id) ON DELETE SET NULL
    );

-- 3. 유저 상태
CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    is_online      BOOLEAN,
    description    VARCHAR(255),
    last_active_at TIMESTAMP WITH TIME ZONE,
                                 user_id        UUID UNIQUE REFERENCES users (id) ON DELETE CASCADE
    );

-- 4. 채널 (채팅방)
CREATE TABLE IF NOT EXISTS channels
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    type            VARCHAR(10)  NOT NULL, -- TEXT, VOICE 등
    last_message_at TIMESTAMP WITH TIME ZONE
                                  );

-- 4-1. 채널 참여자 (채널과 유저의 다대다 연결 테이블)
CREATE TABLE IF NOT EXISTS channel_participants
(
    channel_id UUID NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (channel_id, user_id)
    );

-- 5. 메시지
CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    content    TEXT        NOT NULL,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  UUID        REFERENCES users (id) ON DELETE SET NULL
    );

-- 6. 메시지 첨부파일 (메시지와 파일을 연결)
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
    );

CREATE TABLE IF NOT EXISTS read_statuses
(
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE,
    user_id             UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id          UUID NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at        TIMESTAMP WITH TIME ZONE NOT NULL,
                                                                notification_enabled BOOLEAN NOT NULL,
                                                                UNIQUE (user_id, channel_id)
    );

-- 알림
CREATE TABLE IF NOT EXISTS notifications
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    receiver_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL
    );