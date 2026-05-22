## 요구사항

### 기본 요구사항

#### 프로젝트 버전 및 프론트엔드 갱신
- [x] 프로젝트 버전을 `2.1-M10`으로 변경했습니다.
- [x] 프론트엔드 정적 리소스를 `v2.1.2` 기준으로 반영했습니다.
- [x] `Me API`, `RememberMe` 제거 흐름에 맞춰 백엔드 인증 흐름을 조정했습니다.
- [x] 이미지/첨부 리소스 다운로드가 Blob URL 방식으로 동작하도록 기존 다운로드 API 정합성을 확인했습니다.

#### JWT 컴포넌트 구현
- [x] JWT 의존성을 추가했습니다.
  - `implementation 'com.nimbusds:nimbus-jose-jwt:10.3'`
- [x] `JwtTokenProvider`를 구현했습니다.
  - [x] JWT 발급
  - [x] JWT 서명 검증
  - [x] JWT 만료 검증
  - [x] JWT subject 기반 사용자 식별
- [x] refresh token cookie 이름을 `REFRESH_TOKEN`으로 정의했습니다.

#### 리팩토링 - 로그인
- [x] 세션 생성 정책을 `STATELESS`로 변경했습니다.
- [x] 기존 session concurrency 설정을 제거했습니다.
- [x] 기존 로그인 성공 처리 흐름을 `JwtLoginSuccessHandler`로 대체했습니다.
- [x] 로그인 성공 시 access token을 응답 body와 `Authorization` header에 포함했습니다.
- [x] 로그인 성공 시 refresh token을 `REFRESH_TOKEN` HttpOnly cookie에 저장했습니다.
- [x] `200 JwtDto` 형식으로 응답하도록 구성했습니다.

#### JWT 인증 필터 구현
- [x] `JwtAuthenticationFilter`를 구현했습니다.
- [x] `OncePerRequestFilter`를 상속해 요청당 한 번만 실행되도록 구성했습니다.
- [x] `Authorization: Bearer ...` header가 있는 경우에만 인증을 시도합니다.
- [x] `JwtTokenProvider`로 access token 유효성을 검증합니다.
- [x] `JwtRegistry`로 access token이 active 상태인지 확인합니다.
- [x] 유효한 token이면 `UsernamePasswordAuthenticationToken`을 생성해 `SecurityContextHolder`에 저장합니다.

#### 리프레시 토큰을 활용한 access token 재발급
- [x] `POST /api/auth/refresh` API를 구현했습니다.
- [x] 요청 cookie의 `REFRESH_TOKEN`을 통해 refresh token을 전달받습니다.
- [x] refresh token이 유효한 경우 `200 JwtDto`를 반환합니다.
- [x] refresh token이 유효하지 않은 경우 `401 ErrorResponse`를 반환합니다.
- [x] `/api/auth/refresh`를 `permitAll`에 포함했습니다.
- [x] refresh token rotation을 적용했습니다.
- [x] refresh token rotation 시 기존 access/refresh token 정보를 무효화합니다.

#### 기존 세션 기반 인증 컴포넌트 제거
- [x] `GET /api/auth/me` API를 제거했습니다.
- [x] RememberMe 설정과 필터를 제거했습니다.
- [x] `JSESSIONID` 기반 인증 의존도를 제거하고 JWT 기반 인증 흐름으로 전환했습니다.
- [x] 프론트엔드가 브라우저 메모리에 사용자 정보와 access token을 저장하고, 새로고침 시 refresh token으로 재발급받는 흐름과 정합성을 확인했습니다.

#### 리팩토링 - 로그아웃
- [x] `JwtLogoutHandler`를 구현했습니다.
- [x] logout 시 `REFRESH_TOKEN` cookie를 만료시킵니다.
- [x] logout 요청에 `Authentication` 정보가 없어도 cookie의 refresh token으로 token 정보를 무효화합니다.
- [x] 로그아웃 성공 시 `204 Void`를 반환합니다.

#### 리팩토링 - 토큰 상태 관리
- [x] `JwtRegistry`를 구현했습니다.
- [x] `InMemoryJwtRegistry`를 구현했습니다.
  - [x] `ConcurrentHashMap<UUID, Queue<JwtInformation>>` 기반 저장
  - [x] 사용자별 active token 정보 관리
  - [x] 최대 동시 로그인 수 1개 제한
- [x] `registerJwtInformation`으로 로그인 성공 시 token 정보를 등록합니다.
- [x] `invalidateJwtInformationByUserId`로 사용자별 token 정보를 무효화합니다.
- [x] `hasActiveJwtInformationByUserId`로 사용자 online 여부를 판단합니다.
- [x] `hasActiveJwtInformationByAccessToken`으로 필터에서 active access token 여부를 검사합니다.
- [x] `hasActiveJwtInformationByRefreshToken`으로 refresh API에서 active refresh token 여부를 검사합니다.
- [x] `rotateJwtInformation`으로 refresh token rotation을 수행합니다.
- [x] `clearExpiredJwtInformation`으로 만료 token 정보를 삭제합니다.
- [x] `@EnableScheduling`을 추가했습니다.
- [x] `@Scheduled(fixedDelay = 1000 * 60 * 5)`로 5분마다 만료 token 정보를 정리합니다.

#### 동시 로그인 및 권한 변경 처리
- [x] 동일 계정으로 로그인 시 기존 token 정보를 무효화하도록 리팩토링했습니다.
- [x] 권한이 변경된 사용자가 로그인 상태라면 해당 사용자의 token 정보를 무효화하도록 처리했습니다.
- [x] 사용자 로그인 여부 판단을 기존 `SessionRegistry`에서 `JwtRegistry` 기반으로 변경했습니다.

#### 테스트
- [x] JWT 발급/검증 테스트를 추가했습니다.
- [x] JWT 로그인 성공 handler 테스트를 추가했습니다.
- [x] JWT 인증 필터 테스트를 추가했습니다.
- [x] refresh token 재발급/rotation 통합 테스트를 추가했습니다.
- [x] JWT logout handler 테스트를 추가했습니다.
- [x] JWT registry 테스트를 추가했습니다.
- [x] 정적 리소스 v2.1.2와 주요 API path 정합성을 확인했습니다.
- [x] 전체 테스트를 통과했습니다.
  - `./gradlew test`

---

## 주요 변경사항
- 프로젝트 버전 `2.1-M10` 반영
- 프론트엔드 정적 리소스 `v2.1.2` 반영
- Nimbus 기반 `JwtTokenProvider` 추가
- `JwtDto` 기반 token 로그인 응답 구성
- 세션 생성 정책 `STATELESS` 전환
- 기존 `LoginSuccessHandler`를 `JwtLoginSuccessHandler`로 대체
- `JwtAuthenticationFilter` 추가 및 Bearer token 인증 적용
- `POST /api/auth/refresh` 구현 및 refresh token rotation 적용
- `GET /api/auth/me`, RememberMe 인증 흐름 제거
- `JwtLogoutHandler`로 refresh token cookie 삭제 및 token 무효화 처리
- `JwtRegistry`, `InMemoryJwtRegistry`, `JwtInformation` 추가
- JWT registry 기반 동시 로그인 제한, 권한 변경 시 강제 로그아웃, online 여부 판단 적용
- 만료 token 정보를 5분마다 정리하는 scheduling 추가
- JWT 발급 응답 생성 책임을 `JwtTokenIssuer`로 분리해 login/refresh 중복 제거

## 정적 리소스 정합성
- 프론트엔드에서 호출하는 인증 API와 백엔드 endpoint가 일치하는지 확인했습니다.
  - `/api/auth/login`
  - `/api/auth/logout`
  - `/api/auth/refresh`
  - `/api/auth/role`
- `auth/me`, `remember-me` 호출이 제거된 것을 확인했습니다.
- 첨부/이미지 리소스 흐름을 확인했습니다.
  - metadata: `GET /api/binaryContents/{id}`
  - blob download: `GET /api/binaryContents/{id}/download`
  - frontend: `URL.createObjectURL(...)`
- `/api/auth/csrf-token` 호출은 프론트 호환을 위해 no-op endpoint로 유지했습니다.

## 멘토에게
- JWT 인증으로 전환하면서 access token까지 서버 registry에서 active 여부를 관리하는 방식이 stateless 장점을 일부 포기하는 선택인데, 미션 수준을 넘어 실무에서는 어느 기준으로 blacklist/registry 전략을 선택하는지 궁금합니다.
- refresh token을 HttpOnly cookie로 저장하는 구조에서 CSRF 방어를 `SameSite=Lax`만으로 충분하다고 볼 수 있는지, 별도 CSRF token 또는 `Secure`/domain/path 전략을 어떻게 가져가는지 궁금합니다.
- 현재 access token과 refresh token이 같은 만료 시간을 공유합니다. 실무에서는 보통 두 token의 TTL을 어떻게 분리하고, rotation 실패/재사용 탐지 정책을 어떻게 설계하는지 궁금합니다.
- `InMemoryJwtRegistry`는 서버 재시작이나 다중 인스턴스 환경에서 한계가 있습니다. Redis로 확장할 때 key 구조와 만료 정책을 어떤 식으로 잡는 것이 좋은지 궁금합니다.
- 권한 변경 시 해당 사용자의 token을 전부 무효화하는 방식이 보안상 명확하지만 사용자 경험에는 영향이 있습니다. 권한 변경 이벤트를 token claim/version으로 관리하는 방식과 registry 삭제 방식의 장단점이 궁금합니다.
