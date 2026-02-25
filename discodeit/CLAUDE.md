# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Discord 클론 REST API 서버. Spring Boot 3.5.10 / Java 17 / Gradle 기반.
주요 의존성: `spring-boot-starter-web`, `springdoc-openapi-starter-webmvc-ui:2.8.4`, Lombok

파일 업로드 제한: 단일 파일 10MB, 요청 전체 30MB (`application.yaml`의 `spring.servlet.multipart` 설정).

## 주요 명령어

모든 `./gradlew` 명령은 **`discodeit/` 디렉토리 내**에서 실행해야 합니다 (레포 루트가 아님).

```bash
# 서버 실행 (포트 8080)
./gradlew bootRun

# 빌드
./gradlew build

# 빌드 캐시 초기화 후 빌드
./gradlew clean build

# JUnit 통합 테스트 실행 (Spring 컨텍스트 없이 JCF 저장소 사용)
./gradlew test --tests "com.sprint.mission.discodeit.service.IntegrationServiceTest"

# 전체 테스트 (DiscodeitApplicationTests 포함 — Spring 컨텍스트 로딩 검증)
./gradlew test
```

서버 실행 후 확인:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- React 프론트엔드: `http://localhost:8080`

`FunctionalTest`는 JUnit이 아닌 standalone `main()` 클래스 → IDE에서 `main()` 직접 실행 (`./gradlew test`로는 실행되지 않음).

## 아키텍처 구조

### 레이어 구성

```
HTTP 요청
  ↓
Controller (controller/)         - HTTP 입출력, MultipartFile → 내부 DTO 변환, Swagger 어노테이션
  ↓
Service Interface (service/)     - 비즈니스 로직 계약 (Javadoc으로 명세)
  ↓
BasicXxxService (service/basic/) - 실제 비즈니스 로직
  ↓
Repository Interface (repository/) - 데이터 접근 계약
  ↓
JCFXxxRepository / FileXxxRepository - 실제 저장소 구현체
  ↓
Entity (entity/)                 - 도메인 객체 (BaseEntity 상속)
```

Lombok `@Getter`와 `@RequiredArgsConstructor`가 엔티티·서비스·컨트롤러 전반에 사용됩니다.

### Controller → Service DTO 변환 패턴

서비스 계층은 `MultipartFile`을 직접 받지 않습니다. 컨트롤러에서 private 헬퍼 메서드로 변환 후 전달합니다:

```java
// UserController, MessageController 모두 동일 패턴
private BinaryContentCreateRequest resolveProfile(MultipartFile profile) throws IOException {
    if (profile == null || profile.isEmpty()) return null;
    return new BinaryContentCreateRequest(profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
}
```

`BinaryContentCreateRequest`는 Controller→Service 전달용 내부 DTO이자 `IntegrationServiceTest`에서 직접 인스턴스화하는 테스트 픽스처 역할을 겸합니다.

### 저장소 이중 구현 패턴

`application.yaml`의 `discodeit.repository.type` 값으로 런타임에 구현체가 선택됩니다:

```yaml
discodeit:
  repository:
    type: jcf   # jcf | file
    # file 사용 시 추가 필요:
    # file-directory: .discodeit
```

| 값 | 구현체 | 특징 |
|----|-------|------|
| `jcf` | `JCFXxxRepository` | HashMap 메모리 저장, 재시작 시 데이터 소실, **현재 기본값** |
| `file` | `FileXxxRepository` | Java 직렬화(`.ser`), `{file-directory}/{EntityName}/{uuid}.ser` 경로에 저장 |

`@ConditionalOnProperty`로 조건부 활성화. `matchIfMissing = true`는 `jcf`.
`FileXxxRepository`는 `FileLockProvider`를 통해 파일 경로별 `ReentrantLock`으로 동시성 제어.

### 엔티티 설계 원칙

- 모든 엔티티는 `BaseEntity` 상속 → `id(UUID)`, `createdAt`, `updatedAt` 자동 생성
- `BinaryContent`만 예외: `BaseEntity` 상속 없이 직접 `Serializable` 구현 (불변 객체)
- `serialVersionUID = 1L` 명시: `BaseEntity`, `BinaryContent`, `ReadStatus`, `UserStatus`만. `User`, `Channel`, `Message`는 미선언 → 클래스 구조 변경 시 기존 `.ser` 파일 역직렬화 오류 가능
- `update()` 패턴 차이:
  - `User`, `Channel`, `ReadStatus`: null 체크 + 동등성 체크 → 실제 변경이 있을 때만 `updateTimeStamp()` 호출
  - `Message`, `UserStatus`: null 체크만 → null이 아닌 값이 오면 무조건 업데이트

### 도메인 모델 관계

```
User (1) ──────────── (0..1) BinaryContent  ← 프로필 이미지
  │
  └──(1)── UserStatus                        ← 온라인 상태 추적 (5분 이내 = 온라인)

Channel (1) ─────────── (*) Message
  │                           │
  │                           └──(*) BinaryContent  ← 메시지 첨부파일
  │
  └── ReadStatus (User ↔ Channel 매핑)       ← PRIVATE 채널 참여자 관리에도 사용
```

**삭제 시 연쇄 처리 (JPA cascade 없음, 수동 처리):**
- `User` 삭제 → 프로필 `BinaryContent` 삭제 → `UserStatus` 삭제
- `Channel` 삭제 → 해당 채널 `Message` 삭제 → 각 메시지의 `BinaryContent` 삭제 → `ReadStatus` 삭제
- `Message` 삭제 → 첨부 `BinaryContent` 삭제

### 응답 DTO 사용 규칙

모든 Request/Response DTO는 Java `record`로 구현(불변).

| 상황 | 반환 타입 | 이유 |
|------|----------|------|
| 채널 단건 생성/수정 | `Channel` (엔티티) | 추가 조합 불필요 |
| 채널 목록 조회 | `ChannelDto` | `lastMessageAt`, `participantIds` 조합 필요 |
| 사용자 생성/수정/로그인 | `UserResponse` | 표준 응답 |
| 사용자 목록 조회 | `UserDto` | `online` 상태를 `UserStatus`에서 조합 필요 |
| 메시지, ReadStatus 등 | 엔티티 직접 반환 | 추가 조합 불필요 |

### 예외 처리

`GlobalExceptionHandler`(`@RestControllerAdvice`)가 전역에서 처리:

| 예외 | HTTP 상태 |
|------|----------|
| `NoSuchElementException` | 404 Not Found |
| `IllegalArgumentException` | 400 Bad Request |
| `Exception` (기타) | 500 Internal Server Error |

에러 응답: `{ "message": "...", "timestamp": "..." }` (`ErrorResponse` record)
500 응답은 `GlobalExceptionHandler`에 `@ApiResponses`로 전역 문서화 → 각 컨트롤러에서 별도 선언 불필요.

### 비즈니스 규칙

- `PRIVATE` 채널은 수정 불가 (`IllegalArgumentException`)
- `PRIVATE` 채널 생성 시 참여자 ID 목록으로 `ReadStatus`를 생성하여 참여자 관리
- `Username`과 `Email`은 중복 불가
- `UserStatus.isOnline()`: `lastActiveAt`이 현재 시각 기준 5분 이내이면 온라인
- `UserStatusService`는 전용 컨트롤러 없이 `UserController`의 `PATCH /api/users/{userId}/userStatus`로만 노출

## API 엔드포인트

전체 명세는 `http://localhost:8080/swagger-ui.html` 참조. 요약:

| HTTP | 경로 | 설명 |
|------|------|------|
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/users` | 사용자 등록 (multipart) |
| GET | `/api/users` | 전체 사용자 목록 조회 |
| PATCH | `/api/users/{userId}` | 사용자 정보 수정 (multipart) |
| DELETE | `/api/users/{userId}` | 사용자 삭제 |
| PATCH | `/api/users/{userId}/userStatus` | 온라인 상태 업데이트 |
| POST | `/api/channels/public` | 공개 채널 생성 |
| POST | `/api/channels/private` | 비공개 채널 생성 |
| GET | `/api/channels?userId={uuid}` | 사용자가 접근 가능한 채널 목록 조회 |
| PATCH | `/api/channels/{channelId}` | 채널 수정 (PUBLIC만 가능) |
| DELETE | `/api/channels/{channelId}` | 채널 삭제 |
| POST | `/api/messages` | 메시지 생성 (multipart) |
| GET | `/api/messages?channelId={uuid}` | 채널의 메시지 목록 조회 |
| PATCH | `/api/messages/{messageId}` | 메시지 수정 |
| DELETE | `/api/messages/{messageId}` | 메시지 삭제 |
| POST | `/api/readStatuses` | 읽음 상태 생성 |
| GET | `/api/readStatuses?userId={uuid}` | 사용자의 읽음 상태 목록 조회 |
| PATCH | `/api/readStatuses/{readStatusId}` | 읽음 상태 업데이트 |
| DELETE | `/api/readStatuses/{readStatusId}` | 읽음 상태 삭제 |
| GET | `/api/binaryContents/{id}` | 파일 단건 조회 |
| GET | `/api/binaryContents?binaryContentIds=` | 파일 다건 조회 |

**multipart 업로드 패턴:** JSON 파트와 파일 파트를 `@RequestPart`로 분리 수신.
- 사용자: `@RequestPart("userCreateRequest")` + `@RequestPart(value = "profile", required = false) MultipartFile`
- 메시지: `@RequestPart("messageCreateRequest")` + `@RequestPart(value = "attachments", required = false) List<MultipartFile>`

## 테스트 구조

| 클래스 | 종류 | 실행 방법 |
|--------|------|----------|
| `DiscodeitApplicationTests` | Spring 컨텍스트 로딩 검증 (`@SpringBootTest`) | `./gradlew test` |
| `IntegrationServiceTest` | JUnit 5 (`@Test`), JCF 직접 인스턴스화 | `./gradlew test --tests "...IntegrationServiceTest"` |
| `FunctionalTest` | standalone `main()` 클래스 | IDE에서 직접 실행 |

`IntegrationServiceTest`와 `FunctionalTest`는 Spring 컨텍스트 없이 JCF 저장소를 `new`로 직접 인스턴스화합니다.

## 배포 (Railway.app)

```toml
# railway.toml
[build]
buildCommand = "./gradlew bootJar -x test"

[deploy]
startCommand = "java -jar build/libs/discodeit-0.0.1-SNAPSHOT.jar"
```

**Railway 설정 필수 사항:**
- 서비스 **Root Directory = `discodeit`** (레포 루트가 아님)
- `server.port: ${PORT:8080}` — Railway가 주입하는 `PORT` 환경변수 자동 수신 (별도 설정 불필요)
- Railway 파일시스템은 재시작 시 초기화 → `type: jcf` 유지 필수 (`file` 타입 사용 불가)
