# 디스코드잇 API 문서

디스코드잇 웹 API의 전체 엔드포인트와 사용 방법을 설명합니다.

**Base URL:** `http://localhost:8080`

**Content-Type:** `application/json`

---

## 📑 목차

1. [사용자 관리](#1-사용자-관리)
2. [권한 관리](#2-권한-관리)
3. [채널 관리](#3-채널-관리)
4. [메시지 관리](#4-메시지-관리)
5. [메시지 수신 정보 관리](#5-메시지-수신-정보-관리)
6. [바이너리 파일 관리](#6-바이너리-파일-관리)

---

## 1. 사용자 관리

### 1.1 사용자 등록

사용자를 새로 등록합니다.

```http
POST /users
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "username": "testuser",
  "email": "test@example.com",
  "profileId": null,
  "isOnline": true,
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 1.2 전체 사용자 조회

모든 사용자를 조회합니다.

```http
GET /users
```

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "username": "testuser",
    "email": "test@example.com",
    "profileId": null,
    "isOnline": true,
    "createdAt": "2026-02-09T08:00:00Z",
    "updatedAt": "2026-02-09T08:00:00Z"
  }
]
```

---

### 1.3 특정 사용자 조회

특정 사용자의 정보를 조회합니다.

```http
GET /users/{userId}
```

**Path Parameters:**
- `userId` (UUID, required): 사용자 ID

**Response (200 OK):**
```json
{
  "id": "uuid",
  "username": "testuser",
  "email": "test@example.com",
  "profileId": null,
  "isOnline": true,
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

**Error (404 Not Found):**
```json
{
  "message": "User not found: {userId}",
  "timestamp": "2026-02-09T08:00:00Z"
}
```

---

### 1.4 사용자 정보 수정

사용자 정보를 수정합니다.

```http
PUT /users/{userId}
Content-Type: application/json
```

**Path Parameters:**
- `userId` (UUID, required): 사용자 ID

**Request Body:**
```json
{
  "username": "updated_user",
  "email": "updated@example.com",
  "password": "newpassword456"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "username": "updated_user",
  "email": "updated@example.com",
  "profileId": null,
  "isOnline": true,
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:10:00Z"
}
```

---

### 1.5 사용자 삭제

사용자를 삭제합니다. 연관된 프로필 이미지와 상태 정보도 함께 삭제됩니다.

```http
DELETE /users/{userId}
```

**Path Parameters:**
- `userId` (UUID, required): 사용자 ID

**Response (204 No Content)**

---

### 1.6 온라인 상태 업데이트

사용자의 온라인 상태를 업데이트합니다.

```http
PUT /users/{userId}/status
Content-Type: application/json
```

**Path Parameters:**
- `userId` (UUID, required): 사용자 ID

**Request Body:**
```json
{
  "lastActiveAt": "2026-02-09T08:00:00Z"
}
```

**Response (200 OK):**
```json
{
  "userId": "uuid",
  "lastActiveAt": "2026-02-09T08:00:00Z",
  "id": "uuid",
  "createdAt": "2026-02-09T07:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z",
  "online": true
}
```

---

## 2. 권한 관리

### 2.1 로그인

사용자명과 비밀번호로 로그인합니다.

```http
POST /auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "username": "testuser",
  "email": "test@example.com",
  "profileId": null,
  "isOnline": true,
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

**Error (404 Not Found):**
```json
{
  "message": "Invalid username or password",
  "timestamp": "2026-02-09T08:00:00Z"
}
```

---

## 3. 채널 관리

### 3.1 공개 채널 생성

PUBLIC 타입의 채널을 생성합니다.

```http
POST /channels/public
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "일반 대화",
  "description": "자유롭게 대화하는 채널"
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "type": "PUBLIC",
  "name": "일반 대화",
  "description": "자유롭게 대화하는 채널",
  "participantIds": null,
  "lastMessageAt": null,
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 3.2 비공개 채널 생성

PRIVATE 타입의 채널을 생성합니다. 참여자 목록이 필수입니다.

```http
POST /channels/private
Content-Type: application/json
```

**Request Body:**
```json
{
  "memberIds": [
    "user-uuid-1",
    "user-uuid-2"
  ]
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "type": "PRIVATE",
  "name": null,
  "description": null,
  "participantIds": [
    "user-uuid-1",
    "user-uuid-2"
  ],
  "lastMessageAt": null,
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 3.3 사용자별 채널 목록 조회

특정 사용자가 볼 수 있는 모든 채널을 조회합니다.
- PUBLIC 채널: 모두 조회 가능
- PRIVATE 채널: 참여자만 조회 가능

```http
GET /channels?userId={userId}
```

**Query Parameters:**
- `userId` (UUID, required): 사용자 ID

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "type": "PUBLIC",
    "name": "일반 대화",
    "description": "자유롭게 대화하는 채널",
    "participantIds": null,
    "lastMessageAt": "2026-02-09T07:50:00Z",
    "createdAt": "2026-02-09T07:00:00Z",
    "updatedAt": "2026-02-09T07:00:00Z"
  },
  {
    "id": "uuid",
    "type": "PRIVATE",
    "name": null,
    "description": null,
    "participantIds": ["uuid1", "uuid2"],
    "lastMessageAt": null,
    "createdAt": "2026-02-09T08:00:00Z",
    "updatedAt": "2026-02-09T08:00:00Z"
  }
]
```

---

### 3.4 특정 채널 조회

특정 채널의 상세 정보를 조회합니다.

```http
GET /channels/{channelId}
```

**Path Parameters:**
- `channelId` (UUID, required): 채널 ID

**Response (200 OK):**
```json
{
  "id": "uuid",
  "type": "PUBLIC",
  "name": "일반 대화",
  "description": "자유롭게 대화하는 채널",
  "participantIds": null,
  "lastMessageAt": "2026-02-09T07:50:00Z",
  "createdAt": "2026-02-09T07:00:00Z",
  "updatedAt": "2026-02-09T07:00:00Z"
}
```

---

### 3.5 채널 정보 수정

PUBLIC 채널의 정보를 수정합니다. PRIVATE 채널은 수정할 수 없습니다.

```http
PUT /channels/{channelId}
Content-Type: application/json
```

**Path Parameters:**
- `channelId` (UUID, required): 채널 ID

**Request Body:**
```json
{
  "name": "수정된 채널명",
  "description": "수정된 설명"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "type": "PUBLIC",
  "name": "수정된 채널명",
  "description": "수정된 설명",
  "participantIds": null,
  "lastMessageAt": "2026-02-09T07:50:00Z",
  "createdAt": "2026-02-09T07:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

**Error (400 Bad Request) - PRIVATE 채널 수정 시도:**
```json
{
  "message": "Private channel cannot be updated",
  "timestamp": "2026-02-09T08:00:00Z"
}
```

---

### 3.6 채널 삭제

채널을 삭제합니다. 연관된 메시지, 첨부파일, ReadStatus도 함께 삭제됩니다.

```http
DELETE /channels/{channelId}
```

**Path Parameters:**
- `channelId` (UUID, required): 채널 ID

**Response (204 No Content)**

---

## 4. 메시지 관리

### 4.1 메시지 전송

채널에 새로운 메시지를 전송합니다.

```http
POST /messages
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "안녕하세요! 테스트 메시지입니다.",
  "channelId": "channel-uuid",
  "authorId": "user-uuid"
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "content": "안녕하세요! 테스트 메시지입니다.",
  "channelId": "channel-uuid",
  "authorId": "user-uuid",
  "attachmentIds": [],
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 4.2 채널별 메시지 조회

특정 채널의 모든 메시지를 조회합니다.

```http
GET /messages?channelId={channelId}
```

**Query Parameters:**
- `channelId` (UUID, required): 채널 ID

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "content": "안녕하세요! 테스트 메시지입니다.",
    "channelId": "channel-uuid",
    "authorId": "user-uuid",
    "attachmentIds": [],
    "createdAt": "2026-02-09T08:00:00Z",
    "updatedAt": "2026-02-09T08:00:00Z"
  }
]
```

---

### 4.3 특정 메시지 조회

특정 메시지의 상세 정보를 조회합니다.

```http
GET /messages/{messageId}
```

**Path Parameters:**
- `messageId` (UUID, required): 메시지 ID

**Response (200 OK):**
```json
{
  "id": "uuid",
  "content": "안녕하세요! 테스트 메시지입니다.",
  "channelId": "channel-uuid",
  "authorId": "user-uuid",
  "attachmentIds": [],
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 4.4 메시지 수정

메시지 내용을 수정합니다.

```http
PUT /messages/{messageId}
Content-Type: application/json
```

**Path Parameters:**
- `messageId` (UUID, required): 메시지 ID

**Request Body:**
```json
{
  "content": "수정된 메시지 내용"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "content": "수정된 메시지 내용",
  "channelId": "channel-uuid",
  "authorId": "user-uuid",
  "attachmentIds": [],
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:10:00Z"
}
```

---

### 4.5 메시지 삭제

메시지를 삭제합니다. 연관된 첨부파일도 함께 삭제됩니다.

```http
DELETE /messages/{messageId}
```

**Path Parameters:**
- `messageId` (UUID, required): 메시지 ID

**Response (204 No Content)**

---

## 5. 메시지 수신 정보 관리

### 5.1 수신 정보 생성

특정 채널의 메시지 수신 정보를 생성합니다.

```http
POST /read-status
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user-uuid",
  "channelId": "channel-uuid",
  "lastReadAt": "2026-02-09T08:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "userId": "user-uuid",
  "channelId": "channel-uuid",
  "lastReadAt": "2026-02-09T08:00:00Z",
  "id": "uuid",
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 5.2 수신 정보 수정

메시지 수신 정보를 업데이트합니다.

```http
PUT /read-status/{readStatusId}
Content-Type: application/json
```

**Path Parameters:**
- `readStatusId` (UUID, required): 수신 정보 ID

**Request Body:**
```json
{
  "lastReadAt": "2026-02-09T09:00:00Z"
}
```

**Response (200 OK):**
```json
{
  "userId": "user-uuid",
  "channelId": "channel-uuid",
  "lastReadAt": "2026-02-09T09:00:00Z",
  "id": "uuid",
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T09:00:00Z"
}
```

---

### 5.3 사용자별 수신 정보 조회

특정 사용자의 모든 메시지 수신 정보를 조회합니다.

```http
GET /read-status?userId={userId}
```

**Query Parameters:**
- `userId` (UUID, required): 사용자 ID

**Response (200 OK):**
```json
[
  {
    "userId": "user-uuid",
    "channelId": "channel-uuid-1",
    "lastReadAt": "2026-02-09T08:00:00Z",
    "id": "uuid-1",
    "createdAt": "2026-02-09T07:00:00Z",
    "updatedAt": "2026-02-09T08:00:00Z"
  },
  {
    "userId": "user-uuid",
    "channelId": "channel-uuid-2",
    "lastReadAt": "2026-02-09T07:30:00Z",
    "id": "uuid-2",
    "createdAt": "2026-02-09T07:00:00Z",
    "updatedAt": "2026-02-09T07:30:00Z"
  }
]
```

---

## 6. 바이너리 파일 관리

### 6.1 파일 1개 조회

특정 바이너리 파일을 조회합니다.

```http
GET /binary-contents/{binaryContentId}
```

**Path Parameters:**
- `binaryContentId` (UUID, required): 바이너리 콘텐츠 ID

**Response (200 OK):**
```json
{
  "id": "uuid",
  "fileName": "test.png",
  "contentType": "image/png",
  "data": "base64-encoded-data",
  "createdAt": "2026-02-09T08:00:00Z",
  "updatedAt": "2026-02-09T08:00:00Z"
}
```

---

### 6.2 파일 여러 개 조회

여러 바이너리 파일을 한 번에 조회합니다.

```http
GET /binary-contents?ids={id1},{id2},{id3}
```

**Query Parameters:**
- `ids` (comma-separated UUIDs, required): 바이너리 콘텐츠 ID 목록

**Response (200 OK):**
```json
[
  {
    "id": "uuid-1",
    "fileName": "file1.txt",
    "contentType": "text/plain",
    "data": "base64-encoded-data-1",
    "createdAt": "2026-02-09T08:00:00Z",
    "updatedAt": "2026-02-09T08:00:00Z"
  },
  {
    "id": "uuid-2",
    "fileName": "file2.png",
    "contentType": "image/png",
    "data": "base64-encoded-data-2",
    "createdAt": "2026-02-09T08:00:00Z",
    "updatedAt": "2026-02-09T08:00:00Z"
  }
]
```

---

## 공통 에러 응답

### 400 Bad Request
잘못된 요청 형식

```json
{
  "message": "Invalid request format",
  "timestamp": "2026-02-09T08:00:00Z"
}
```

### 404 Not Found
리소스를 찾을 수 없음

```json
{
  "message": "Resource not found: {resourceId}",
  "timestamp": "2026-02-09T08:00:00Z"
}
```

### 500 Internal Server Error
서버 내부 오류

```json
{
  "message": "서버 내부 오류가 발생했습니다: {error details}",
  "timestamp": "2026-02-09T08:00:00Z"
}
```

---

## 테스트 순서 권장사항

### 기본 플로우
1. 사용자 등록
2. 로그인
3. 공개 채널 생성
4. 메시지 전송
5. 메시지 조회
6. 메시지 수정
7. 온라인 상태 업데이트

### 비공개 채널 플로우
1. 사용자 2명 등록
2. 비공개 채널 생성 (두 사용자 초대)
3. 각 사용자별 채널 목록 조회
4. 메시지 수신 정보 생성
5. 메시지 전송 및 조회

---

## 추가 리소스

- **Postman Collection**: `Discodeit_API_Collection.postman_collection.json`
- **Environment File**: `Discodeit_Environment.postman_environment.json`
- **Python 자동화 스크립트**: `test_api_automation.py`
- **Bash 테스트 스크립트**: `/tmp/test_discodeit_api.sh`

---

**문서 버전**: 1.0
**최종 수정일**: 2026-02-09
