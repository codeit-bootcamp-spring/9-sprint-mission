# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Discord 클론 REST API 서버. Spring Boot 3.5.10 / Java 17 / Gradle 기반.

## 주요 명령어

```bash
# 서버 실행 (포트 8080)
./gradlew bootRun

# 빌드
./gradlew build

# 빌드 캐시 초기화 후 빌드
./gradlew clean build

# JUnit 통합 테스트 실행 (Spring 컨텍스트 없이 JCF 저장소 사용)
./gradlew test --tests "com.sprint.mission.discodeit.service.IntegrationServiceTest"

# FunctionalTest는 JUnit이 아닌 standalone main 클래스 → IDE에서 main() 직접 실행
# ./gradlew test 로는 실행되지 않음
```

서버 실행 후 확인:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- React 프론트엔드: `http://localhost:8080`

## 아키텍처 구조

### 레이어 구성

```
HTTP 요청
  ↓
Controller (controller/)         - HTTP 입출력, Swagger 어노테이션
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

### 저장소 이중 구현 패턴

`application.yaml`의 `discodeit.repository.type` 값으로 런타임에 구현체가 선택됩니다:

```yaml
discodeit:
  repository:
    type: file      # file | jcf
    file-directory: .discodeit
```

| 값 | 구현체 | 특징 |
|----|-------|------|
| `jcf` | `JCFXxxRepository` | HashMap 메모리 저장, 재시작 시 데이터 소실, **기본값** |
| `file` | `FileXxxRepository` | Java 직렬화(.ser 파일), `{file-directory}/{EntityName}/{uuid}.ser` 경로에 저장 |

`@ConditionalOnProperty`로 조건부 활성화. JCF가 `matchIfMissing = true`(기본값).
`FileXxxRepository`는 `FileLockProvider`를 통해 파일 경로별 `ReentrantLock`으로 동시성을 제어합니다.

### 엔티티 설계 원칙

- 모든 엔티티는 `BaseEntity`를 상속 → `id(UUID)`, `createdAt`, `updatedAt` 자동 생성
- `BinaryContent`만 예외적으로 `BaseEntity`를 상속하지 않고 직접 `Serializable` 구현 (불변 객체)
- 파일 저장소 직렬화를 위해 `Serializable` 구현 필수, `serialVersionUID = 1L` 선언
- 수정 메서드(`update()`)는 **null이 아니고 기존 값과 다른 경우에만** 필드를 업데이트하며, **실제 변경이 있을 때만** `updateTimeStamp()` 호출

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

**삭제 시 연쇄 처리 (cascade 없음, 수동 처리):**
- `User` 삭제 → 프로필 `BinaryContent` 삭제 → `UserStatus` 삭제
- `Channel` 삭제 → 해당 채널 `Message` 삭제 → 각 메시지의 `BinaryContent` 삭제 → `ReadStatus` 삭제
- `Message` 삭제 → 첨부 `BinaryContent` 삭제

### 응답 DTO 사용 규칙

| 상황 | 반환 타입 | 이유 |
|------|----------|------|
| 채널 단건 생성/수정 | `Channel` (엔티티) | 추가 조합 불필요 |
| 채널 목록 조회 | `ChannelDto` | `lastMessageAt`, `participantIds` 조합 필요 |
| 사용자 생성/수정/로그인 | `UserResponse` | 표준 응답 |
| 사용자 목록 조회 | `UserDto` | `online` 상태를 `UserStatus`에서 조합 필요 |
| 메시지, ReadStatus 등 | 엔티티 직접 반환 | 추가 조합 불필요 |

`ChannelResponse`, `MessageResponse`는 현재 서비스/컨트롤러 계층에서 사용되지 않고
`IntegrationServiceTest`, `FunctionalTest`에서만 참조됩니다 (테스트와 실제 구현 간 불일치 주의).

### 예외 처리

`GlobalExceptionHandler`(`@RestControllerAdvice`)가 전역에서 예외를 처리합니다:

| 예외 | HTTP 상태 |
|------|----------|
| `NoSuchElementException` | 404 Not Found |
| `IllegalArgumentException` | 400 Bad Request |
| `Exception` (기타) | 500 Internal Server Error |

에러 응답 형식: `{ "message": "...", "timestamp": "..." }` (`ErrorResponse` record)
500 응답은 `GlobalExceptionHandler`에 `@ApiResponses`로 전역 문서화되어 있어 각 컨트롤러에서 별도 선언 불필요.

### 비즈니스 규칙

- `PRIVATE` 채널은 수정 불가 (`IllegalArgumentException` 발생)
- `PRIVATE` 채널 생성 시 참여자 ID 목록으로 `ReadStatus`를 생성하여 참여자 관리
- `Username`과 `Email`은 중복 불가
- `UserStatus.isOnline()`: `lastActiveAt`이 현재 시각 기준 5분 이내이면 온라인

## 파일/패키지 역할 요약

| 경로 | 역할 |
|------|------|
| `controller/` | REST API 엔드포인트. `@PostMapping`, `@GetMapping` 등. Swagger `@Operation`/`@ApiResponses` 포함 |
| `service/` | 인터페이스 선언 (Javadoc으로 계약 명시) |
| `service/basic/` | `Basic{Domain}Service` — 실제 구현체 |
| `repository/` | 저장소 인터페이스 |
| `repository/jcf/` | HashMap 기반 메모리 저장소 |
| `repository/file/` | Java 직렬화 파일 저장소. `FileLockProvider`로 동시성 제어 |
| `entity/` | 도메인 객체. `BaseEntity` 상속 필수 (`BinaryContent` 제외) |
| `dto/request/` | Java Record 기반 요청 DTO. 수정 요청 필드는 `new` 접두사 (예: `newName`, `newContent`) |
| `dto/response/` | `UserResponse`, `UserDto`, `ChannelDto`, `ChannelResponse`, `MessageResponse`, `ErrorResponse` |
| `exception/` | `GlobalExceptionHandler`, `ErrorResponse` |
| `config/SwaggerConfig.java` | Swagger OpenAPI 문서 설정 |
| `resources/static/` | React 빌드 결과물 (수정 불필요) |

## 테스트 구조 주의사항

| 클래스 | 종류 | 실행 방법 |
|--------|------|----------|
| `IntegrationServiceTest` | JUnit 5 (`@Test`) | `./gradlew test` |
| `FunctionalTest` | standalone `main()` 클래스 | IDE에서 직접 실행 |

두 테스트 파일 모두 JCF 저장소를 직접 인스턴스화하여 Spring 컨텍스트 없이 동작합니다.

**현재 알려진 테스트-구현 불일치:**
- 테스트에서 `ChannelUpdateRequest`를 참조하지만 실제 DTO 이름은 `PublicChannelUpdateRequest`
- 테스트에서 `userService.findAll()`을 호출하지만 인터페이스 메서드 이름은 `findAllAsDto()`
- 테스트에서 `channelService.createPublic()`이 `ChannelResponse`를 반환한다고 가정하지만 실제 반환 타입은 `Channel`

이 불일치로 인해 `./gradlew test` 실행 시 컴파일 오류가 발생할 수 있습니다.
