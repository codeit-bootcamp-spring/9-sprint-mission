## 요구사항

### 기본 요구사항

#### 프로젝트 버전 및 프론트엔드 갱신
- [x] 프로젝트 버전 `2.1-M10` 반영
- [x] 프론트엔드 정적 리소스 `v2.1.2` 반영
- [x] `Me API`, `RememberMe` 제거 흐름에 맞춘 백엔드 인증 흐름 변경
- [x] 이미지/첨부 리소스 다운로드 API 정합성 확인

#### JWT 컴포넌트 구현
- [x] JWT 의존성 추가
  - `implementation 'com.nimbusds:nimbus-jose-jwt:10.3'`
- [x] `JwtTokenProvider` 구현
  - [x] JWT 발급
  - [x] JWT 서명 검증
  - [x] JWT 만료 검증
  - [x] JWT subject 기반 사용자 식별
- [x] refresh token cookie 이름 `REFRESH_TOKEN` 정의

#### 리팩토링 - 로그인
- [x] 세션 생성 정책 `STATELESS` 변경
- [x] 기존 session concurrency 설정 제거
- [x] 기존 로그인 성공 처리 흐름을 `JwtLoginSuccessHandler`로 대체
- [x] 로그인 성공 시 access token을 응답 body와 `Authorization` header에 포함
- [x] 로그인 성공 시 refresh token을 `REFRESH_TOKEN` HttpOnly cookie에 저장
- [x] `200 JwtDto` 응답 구성

#### JWT 인증 필터 구현
- [x] `JwtAuthenticationFilter` 구현
- [x] `OncePerRequestFilter` 상속으로 요청당 1회 실행
- [x] `Authorization: Bearer ...` header 기반 인증 시도
- [x] `JwtTokenProvider` 기반 access token 유효성 검증
- [x] `JwtRegistry` 기반 active access token 확인
- [x] 유효 token 인증 정보를 `SecurityContextHolder`에 저장

#### 리프레시 토큰을 활용한 access token 재발급
- [x] `POST /api/auth/refresh` API 구현
- [x] 요청 cookie의 `REFRESH_TOKEN` 기반 refresh token 전달
- [x] refresh token 유효 시 `200 JwtDto` 반환
- [x] refresh token 무효 시 `401 ErrorResponse` 반환
- [x] `/api/auth/refresh` `permitAll` 반영
- [x] refresh token rotation 적용
- [x] rotation 시 기존 access/refresh token 정보 무효화

#### 기존 세션 기반 인증 컴포넌트 제거
- [x] `GET /api/auth/me` API 제거
- [x] RememberMe 설정 및 필터 제거
- [x] `JSESSIONID` 기반 인증 의존도 제거
- [x] 브라우저 메모리 기반 사용자 정보/access token 저장 흐름 확인
- [x] refresh token 기반 재발급 흐름 정합성 확인

#### 리팩토링 - 로그아웃
- [x] `JwtLogoutHandler` 구현
- [x] logout 시 `REFRESH_TOKEN` cookie 만료
- [x] `Authentication` 없이 cookie refresh token 기반 token 정보 무효화
- [x] 로그아웃 성공 시 `204 Void` 반환

#### 리팩토링 - 토큰 상태 관리
- [x] `JwtRegistry` 구현
- [x] `InMemoryJwtRegistry` 구현
  - [x] `ConcurrentHashMap<UUID, Queue<JwtInformation>>` 기반 저장
  - [x] 사용자별 active token 정보 관리
  - [x] 최대 동시 로그인 수 1개 제한
- [x] `registerJwtInformation` 기반 로그인 성공 token 정보 등록
- [x] `invalidateJwtInformationByUserId` 기반 사용자별 token 정보 무효화
- [x] `hasActiveJwtInformationByUserId` 기반 사용자 online 여부 판단
- [x] `hasActiveJwtInformationByAccessToken` 기반 필터 active access token 검사
- [x] `hasActiveJwtInformationByRefreshToken` 기반 refresh API active refresh token 검사
- [x] `rotateJwtInformation` 기반 refresh token rotation
- [x] `clearExpiredJwtInformation` 기반 만료 token 정보 삭제
- [x] `@EnableScheduling` 추가
- [x] `@Scheduled(fixedDelay = 1000 * 60 * 5)` 기반 5분 주기 만료 token 정리

#### 동시 로그인 및 권한 변경 처리
- [x] 동일 계정 로그인 시 기존 token 정보 무효화
- [x] 권한 변경 사용자 token 정보 무효화
- [x] 사용자 로그인 여부 판단 기준을 `SessionRegistry`에서 `JwtRegistry`로 변경

#### 테스트
- [x] JWT 발급/검증 테스트 추가
- [x] JWT 로그인 성공 handler 테스트 추가
- [x] JWT 인증 필터 테스트 추가
- [x] refresh token 재발급/rotation 통합 테스트 추가
- [x] JWT logout handler 테스트 추가
- [x] JWT registry 테스트 추가
- [x] 정적 리소스 `v2.1.2`와 주요 API path 정합성 확인
- [x] 전체 테스트 통과
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
- `JwtLogoutHandler` 기반 refresh token cookie 삭제 및 token 무효화
- `JwtRegistry`, `InMemoryJwtRegistry`, `JwtInformation` 추가
- JWT registry 기반 동시 로그인 제한, 권한 변경 시 강제 로그아웃, online 여부 판단 적용
- 5분 주기 만료 token 정리 scheduling 추가
- `JwtTokenIssuer` 기반 JWT 발급 응답 생성 책임 분리

## 정적 리소스 정합성
- 프론트엔드 호출 인증 API와 백엔드 endpoint 일치 확인
  - `/api/auth/login`
  - `/api/auth/logout`
  - `/api/auth/refresh`
  - `/api/auth/role`
- `auth/me`, `remember-me` 호출 제거 확인
- 첨부/이미지 리소스 흐름 확인
  - metadata: `GET /api/binaryContents/{id}`
  - blob download: `GET /api/binaryContents/{id}/download`
  - frontend: `URL.createObjectURL(...)`
- `/api/auth/csrf-token` 프론트 호환용 no-op endpoint 유지

## 멘토에게
- 현재 `InMemoryJwtRegistry`로 token 상태를 관리하고 있는데, 실무에서 서버가 여러 대로 늘어나면 Redis 같은 외부 저장소로 옮겨야 할 것 같습니다. 이때 userId/accessToken/refreshToken 기준 key를 어떻게 설계하는 것이 운영과 만료 처리 측면에서 좋은지 궁금합니다.
- refresh token을 HttpOnly cookie로 저장하고 `SameSite=Lax`를 설정했습니다. 실제 서비스 배포 환경에서는 `Secure`, domain, path, CSRF 대응을 어떤 기준으로 함께 점검하는지 궁금합니다.
- refresh token rotation은 구현했지만, 이전 refresh token이 다시 사용된 상황을 현재는 401로만 처리합니다. 실무에서는 이런 재사용 시도를 탈취 의심으로 보고 해당 사용자의 token을 전부 무효화하거나 로그를 남기는지 궁금합니다.
