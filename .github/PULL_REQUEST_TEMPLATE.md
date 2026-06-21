## 요구사항

### SSE 기반 실시간 이벤트 전송
- [x] `GET /api/sse` SSE 연결 API 구현
- [x] 인증된 사용자만 SSE 연결을 생성하도록 처리
- [x] `SseEmitterRepository`로 사용자별 SSE 연결 관리
- [x] `SseMessageRepository`로 최근 SSE 이벤트 저장
- [x] `Last-Event-ID` header / query parameter 기반 누락 이벤트 재전송
- [x] ping 이벤트와 scheduled cleanup으로 끊어진 emitter 정리
- [x] 알림 생성, 바이너리 업로드 상태 변경, 채널 변경, 사용자 변경 이벤트를 SSE로 전송

### WebSocket 기반 메시지 송수신
- [x] Spring WebSocket STOMP 설정 추가
- [x] `/ws` WebSocket endpoint 등록
- [x] `/pub` publish prefix, `/sub` subscribe broker prefix 설정
- [x] `@MessageMapping("/messages")` 기반 메시지 생성 처리
- [x] 인증 사용자와 메시지 작성자 불일치 시 차단
- [x] 메시지 생성 이벤트를 `/sub/channels.{channelId}.messages` destination으로 전송
- [x] WebSocket inbound channel에 JWT 인증과 권한 검증 적용

### WebSocket JWT 인증 / 인가
- [x] `JwtAuthenticationChannelInterceptor` 추가
- [x] STOMP `CONNECT` 요청의 `Authorization: Bearer ...` header 검증
- [x] JWT registry에서 활성 access token 여부 확인
- [x] token username 기반 `UserDetails` 로드 후 STOMP user 설정
- [x] Spring Security Messaging 기반 `ROLE_USER` 권한 검사 적용
- [x] WebSocket 인증 / 권한 테스트 추가

### Redis 기반 JWT Registry
- [x] `DISCODEIT_JWT_REGISTRY_TYPE=redis` 설정 기반 `RedisJwtRegistry` 선택 가능하도록 구현
- [x] 사용자별 JWT 정보를 Redis list로 관리
- [x] access token / refresh token 빠른 조회를 위한 Redis set index 추가
- [x] refresh token rotation 시 기존 token index 제거 후 새 token 등록
- [x] 사용자별 동시 활성 token 수 제한 유지
- [x] Redis lock과 Spring Retry로 동시 로그인 / 갱신 경쟁 상황 보강
- [x] 만료된 JWT 정보 scheduled cleanup 처리
- [x] 로그인 / 로그아웃 handler가 Redis registry에서도 동작하도록 테스트 보강

### Kafka 기반 실시간 이벤트 중계
- [x] 실시간 이벤트 전용 Kafka topic 정의
- [x] `discodeit.realtime.kafka.enabled` 옵션 추가
- [x] Spring Event 기반 실시간 listener와 Kafka 기반 listener를 profile / property로 전환 가능하도록 구성
- [x] 트랜잭션 commit 이후 실시간 이벤트를 Kafka topic으로 발행
- [x] Kafka topic을 소비해 WebSocket / SSE 클라이언트로 재전송
- [x] 메시지 생성 이벤트는 WebSocket destination으로 전송
- [x] 알림 / 바이너리 / 채널 / 사용자 변경 이벤트는 SSE로 전송
- [x] Kafka producer / consumer 흐름 테스트 추가

### 분산 배포 Docker Compose / Nginx 구성
- [x] backend replica를 여러 개 띄울 수 있도록 Compose 구성 변경
- [x] Nginx reverse proxy 컨테이너 추가
- [x] Nginx upstream으로 backend replica 로드밸런싱 구성
- [x] `/api/sse`, `/api`, `/ws` proxy 설정 추가
- [x] WebSocket upgrade header와 SSE buffering off 설정 적용
- [x] backend / db / redis / kafka를 내부 network로 격리
- [x] PostgreSQL, Redis, Kafka, backend storage volume 구성
- [x] 운영 실행 시 Redis JWT registry와 Kafka realtime listener를 사용하도록 환경 변수 정리

### 테스트 / 정리
- [x] SSE service, repository, listener 테스트 추가
- [x] WebSocket controller, event listener, JWT channel interceptor 테스트 추가
- [x] Kafka 실시간 topic listener 테스트 추가
- [x] 기존 알림 / 바이너리 업로드 / 사용자 / 채널 서비스 테스트 보강
- [x] frontend 정적 asset 갱신
- [x] 프로젝트 버전 `3.0-M12` 반영

---

## 주요 변경사항

- SSE 연결 API를 추가해 서버에서 클라이언트로 알림, 바이너리 업로드 상태, 채널 / 사용자 변경 이벤트를 실시간 전송하도록 구현했습니다.
- SSE 연결이 끊겼다가 재연결되는 상황을 고려해 `Last-Event-ID` 기반 누락 이벤트 재전송 흐름을 추가했습니다.
- WebSocket STOMP 기반 메시지 생성 흐름을 추가하고, 생성된 메시지를 채널별 구독 destination으로 전송하도록 구현했습니다.
- WebSocket 연결 시 JWT access token을 검증하고, Spring Security Messaging으로 `ROLE_USER` 권한을 확인하도록 구성했습니다.
- 기존 in-memory JWT registry 외에 Redis 기반 JWT registry를 추가해 여러 backend replica가 같은 로그인 / 로그아웃 / token rotation 상태를 공유할 수 있도록 했습니다.
- Kafka를 실시간 이벤트 중계 계층으로 추가해 한 backend replica에서 발생한 이벤트가 다른 replica에 연결된 SSE / WebSocket 클라이언트에게도 전달되도록 했습니다.
- Nginx reverse proxy와 Docker Compose replica 구성을 추가해 분산 배포 환경에서 SSE, WebSocket, Redis, Kafka가 함께 동작하는 구조를 구성했습니다.

---

## 멘토에게

- SSE 재전송 이력을 현재는 application memory에 보관하고 있는데, 실무에서는 Redis Stream이나 별도 event store로 분리하는 기준이 궁금합니다.
- WebSocket STOMP 인증을 `CONNECT` 시점에만 검증하고 있는데, 장시간 연결에서 token 만료를 어떻게 처리하는 방식이 일반적인지 궁금합니다.
- 여러 backend replica가 같은 Kafka topic을 소비해 각자 연결된 클라이언트로 이벤트를 보내는 구조에서 consumer group / broadcast 전략을 어떻게 설계하는 것이 적절한지 궁금합니다.
- Redis JWT registry에서 access / refresh token index를 set으로 관리했는데, token TTL과 index 정합성을 더 안전하게 관리하는 실무 패턴이 궁금합니다.
