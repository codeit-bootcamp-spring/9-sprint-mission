## 요구사항

### Spring Event - 파일 업로드 로직 분리
- [x] `BinaryContentCreatedEvent` 정의
- [x] `BinaryContentStorage.put` 직접 호출 대신 이벤트 발행 구조로 변경
- [x] `BinaryContent`에 업로드 상태 추가
  - `PROCESSING`
  - `SUCCESS`
  - `FAIL`
- [x] 메타데이터 저장 트랜잭션 커밋 이후 바이너리 업로드 수행
- [x] 업로드 성공 시 `SUCCESS`, 실패 시 `FAIL` 반영
- [x] 업로드 실패 시 상태 변경 및 실패 정보 기록

### 알림 기능
- [x] `MessageCreatedEvent` 정의 및 메시지 생성 시 이벤트 발행
- [x] `RoleUpdatedEvent` 정의 및 권한 변경 시 이벤트 발행
- [x] `ReadStatus.notificationEnabled` 추가
- [x] 채널 타입에 따른 알림 기본값 적용
  - PRIVATE: `true`
  - PUBLIC: `false`
- [x] `ReadStatusUpdateRequest`에 알림 설정 수정 필드 추가
- [x] 알림 조회 API 구현
  - `GET /api/notifications`
- [x] 알림 확인 API 구현
  - `DELETE /api/notifications/{notificationId}`
- [x] 메시지 생성 / 권한 변경 이벤트 기반 알림 생성

### 비동기 처리
- [x] `AsyncConfig` 추가
- [x] `@EnableAsync` 적용
- [x] `TaskExecutor` Bean 등록
- [x] `TaskDecorator`로 MDC Request ID, SecurityContext 전파
- [x] 이벤트 리스너 비동기 처리 적용
- [x] 메시지 생성 API `@Timed` 적용
- [x] Actuator observation 설정 추가

### 비동기 실패 처리
- [x] Spring Retry 의존성 추가
- [x] `@EnableRetry` 적용
- [x] S3 업로드 재시도 정책 적용
- [x] `@Recover` 기반 최종 실패 처리
- [x] S3 업로드 실패 이벤트 및 관리자 알림 처리

### Redis Cache
- [x] Caffeine cache 설정
- [x] Redis 전역 cache 설정
- [x] 사용자별 채널 목록 캐시 적용
- [x] 사용자별 알림 목록 캐시 적용
- [x] 사용자 목록 캐시 적용
- [x] 데이터 변경 시 캐시 무효화 처리
- [x] Redis cache statistics 및 Actuator cache metrics 확인 가능하도록 설정

### Kafka
- [x] Kafka Docker Compose 구성
- [x] Spring Kafka 의존성 추가
- [x] Kafka producer / consumer 설정 추가
- [x] Spring Event를 Kafka topic으로 발행하는 중계 리스너 구현
- [x] 기존 Spring Event 기반 알림 리스너 비활성화 옵션 추가
- [x] Kafka topic 구독 기반 알림 생성 리스너 구현
- [x] 메시지 생성, 권한 변경, S3 업로드 실패 이벤트 Kafka 처리 적용

### DB / 개발 환경
- [x] PostgreSQL 개발 환경 구성
- [x] Flyway 기반 schema migration 도입
- [x] `schema.sql` 정리 및 제약조건 보완
- [x] dev / test / prod profile 설정 정리
- [x] Redis / Kafka / PostgreSQL Docker Compose 구성 정리
- [x] 로컬 스토리지 경로 정규화 및 로그 추가

---

## 주요 변경사항

- 파일 업로드 로직을 메타데이터 저장 트랜잭션과 분리했습니다.
- `BinaryContent` 업로드 상태를 `PROCESSING`, `SUCCESS`, `FAIL`로 관리하도록 변경했습니다.
- 메시지 생성, 권한 변경, S3 업로드 실패 이벤트를 기반으로 알림을 생성하도록 구현했습니다.
- 알림 API를 추가했습니다.
  - `GET /api/notifications`
  - `DELETE /api/notifications/{notificationId}`
- 이벤트 리스너를 비동기로 처리하도록 `AsyncConfig`를 추가했습니다.
- MDC Request ID와 SecurityContext가 비동기 스레드에서도 유지되도록 `TaskDecorator`를 적용했습니다.
- S3 업로드 실패에 대해 Spring Retry와 `@Recover` 기반 후속 처리를 추가했습니다.
- Caffeine local cache에서 Redis global cache로 전환해 다중 서버 환경에서도 cache를 공유할 수 있도록 구성했습니다.
- Kafka 기반 이벤트 발행 / 소비 구조를 추가했습니다.
- PostgreSQL 개발 환경과 Flyway migration을 도입했습니다.
- 로컬 파일 다운로드 404 문제를 추적하기 쉽도록 local storage 경로를 절대경로로 정규화하고 시작 로그를 추가했습니다.

---

## 멘토에게

- 현재는 하나의 애플리케이션 안에서 Kafka producer와 consumer를 모두 구현했지만, 실제로 알림 서비스를 분리한다면 event payload를 어느 정도까지 풍부하게 가져가는 것이 좋을지 궁금합니다.
- Redis cache eviction을 서비스 메소드에서 직접 처리하고 있는데, 실무에서는 도메인 이벤트 기반으로 cache invalidation을 분리하는 방식도 자주 사용하는지 궁금합니다.
- 파일 업로드 실패 시 현재는 상태를 `FAIL`로 남기고 관리자 알림을 생성하는 구조인데, 사용자에게 재시도 버튼을 제공하려면 어떤 API 설계가 적절할지 궁금합니다.
