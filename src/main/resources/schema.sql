-- ===== Discodeit 데이터베이스 스키마 =====
-- PostgreSQL 대상, ddl-auto: validate 모드이므로 수동 적용 필요
-- 실행: psql -U discodeit_user -d discodeit -f src/main/resources/schema.sql

-- 파일 메타데이터 (프로필 이미지, 메시지 첨부파일)
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID         NOT NULL PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

-- 사용자
CREATE TABLE IF NOT EXISTS users
(
    id         UUID        NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(60) NOT NULL,
    profile_id UUID REFERENCES binary_contents (id) ON DELETE SET NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- 사용자 온라인 상태 (User와 1:1)
CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID        NOT NULL PRIMARY KEY,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    user_id        UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_user_statuses_user_id UNIQUE (user_id)
);

-- 채널 (PUBLIC/PRIVATE)
CREATE TABLE IF NOT EXISTS channels
(
    id          UUID        NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
);

-- 읽음 상태 (사용자-채널별 마지막 읽음 시각)
CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID        NOT NULL PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

-- 메시지
CREATE TABLE IF NOT EXISTS messages
(
    id         UUID        NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    TEXT,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  UUID REFERENCES users (id) ON DELETE SET NULL
);

-- 메시지-첨부파일 중간 테이블 (N:M)
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
);
