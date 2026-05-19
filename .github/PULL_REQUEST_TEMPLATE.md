## 요구사항

### 기본 요구사항

#### Spring Security 기본 설정
- [x] `SecurityConfig` 클래스를 생성하세요.
- [x] `SecurityFilterChain` Bean을 선언하세요.
- [x] 개발 환경에서 Spring Security 모듈의 로깅 레벨을 trace로 설정하세요.
- [x] 기본 SecurityFilterChain 등록 시 추가되는 필터 목록을 확인하세요.

#### CSRF 보호 설정
- [x] `CsrfTokenRepository` 구현체를 `CookieCsrfTokenRepository`로 설정하세요.
- [x] 클라이언트가 CSRF 토큰 쿠키에 접근할 수 있도록 HttpOnly를 false로 설정하세요.
- [x] CSR/SPA 환경에 적합한 `CsrfTokenRequestHandler`를 정의하세요.
- [x] CSRF 토큰 발급 API를 구현하세요.
  - [x] `GET /api/auth/csrf-token`
  - [x] 응답: `203 Void`

#### 회원가입
- [x] 기존 회원가입 API 스펙을 유지하세요.
- [x] 회원가입 시 비밀번호는 `PasswordEncoder`를 통해 해시로 저장하세요.
- [x] `PasswordEncoder` 구현체는 `BCryptPasswordEncoder`를 활용하세요.

#### 인증 - 로그인
- [x] `formLogin`을 기본값으로 활성화하세요.
- [x] 로그인 처리 URL을 `/api/auth/login`으로 설정하세요.
- [x] `UserDetailsService` 구현체를 정의하세요.
- [x] `UserDetails` 구현체를 정의하세요.
- [x] 로그인 성공 시 `200 UserDto`로 응답하는 `AuthenticationSuccessHandler`를 정의하세요.
- [x] 로그인 실패 시 `401 ErrorResponse`로 응답하는 `AuthenticationFailureHandler`를 정의하세요.
- [x] 기존 로그인 관련 코드를 제거하세요.

#### 로그인 고도화 - RememberMe
- [x] 로그인 요청 파라미터 `remember-me=true`인 경우 RememberMe 인증을 활성화하세요.
- [x] 세션이 무효화되어도 RememberMe 쿠키로 다시 인증될 수 있도록 설정하세요.
- [x] RememberMe 서명 key를 환경변수로 설정할 수 있도록 구성하세요.

#### 인증 - 현재 사용자 정보 조회
- [x] 세션 ID를 통해 현재 사용자 정보를 조회하는 API를 구현하세요.
  - [x] `GET /api/auth/me`
  - [x] 응답: `200 UserDto`
- [x] `@AuthenticationPrincipal`을 통해 인증 정보에 접근하세요.

#### 인증 - 로그아웃
- [x] 로그아웃 처리 URL을 `/api/auth/logout`으로 설정하세요.
- [x] `LogoutSuccessHandler`를 `HttpStatusReturningLogoutSuccessHandler`로 대체하세요.
- [x] 로그아웃 성공 시 `204 Void`를 반환하세요.
- [x] 로그아웃 시 세션을 무효화하고 `JSESSIONID` 쿠키를 삭제하세요.

#### 인가 - 권한 정의
- [x] 권한을 정의하세요.
  - [x] 관리자: `ADMIN`
  - [x] 채널 매니저: `CHANNEL_MANAGER`
  - [x] 일반 사용자: `USER`
- [x] 데이터베이스 스키마에 사용자 권한 컬럼을 추가하세요.
- [x] 회원가입 시 모든 사용자는 `USER` 권한을 기본 권한으로 설정하세요.
- [x] 사용자 권한 수정 API를 구현하세요.
  - [x] `PUT /api/auth/role`
  - [x] 요청: `UserRoleUpdateRequest`
  - [x] 응답: `200 UserDto`
- [x] 애플리케이션 실행 시 ADMIN 권한을 가진 어드민 계정이 초기화되도록 구현하세요.
- [x] 초기 관리자 계정은 운영 복구 수단으로 보호되도록 권한 변경을 제한하세요.
- [x] 관리자가 자기 자신의 권한을 낮출 수 없도록 제한하세요.
- [x] `DiscodeitUserDetails.getAuthorities()`를 수정하세요.

#### 인가 - 권한 적용
- [x] `authorizeHttpRequests`를 활성화하고 API 요청 인증을 설정하세요.
- [x] 인증 없이 접근 가능한 요청을 설정하세요.
  - [x] CSRF Token 발급
  - [x] 회원가입
  - [x] 로그인
  - [x] 로그아웃
  - [x] API가 아닌 요청
- [x] Method Security를 활성화하세요.
- [x] 퍼블릭 채널 생성, 수정, 삭제는 `CHANNEL_MANAGER` 권한을 요구하도록 설정하세요.
- [x] 사용자 권한 수정은 `ADMIN` 권한을 요구하도록 설정하세요.
- [x] 권한이 없는 경우 `403 ErrorResponse`를 반환하세요.
- [x] `RoleHierarchy`를 활용해 권한 계층 구조를 정의하세요.
  - [x] `ADMIN > CHANNEL_MANAGER > USER`

#### 세션 관리 고도화
- [x] 동일한 계정으로 동시 로그인할 수 없도록 설정하세요.
- [x] `sessionConcurrency` 설정을 활용하세요.
- [x] 세션 동일성 보장을 위해 `DiscodeitUserDetails.equals()`와 `hashCode()`를 오버라이딩하세요.
- [x] 권한이 변경된 사용자가 로그인 상태라면 세션을 무효화하세요.
- [x] `SessionRegistry` Bean을 등록하고 `SecurityFilterChain`에 연결하세요.
- [x] `HttpSessionEventPublisher` Bean을 등록하세요.
- [x] `UserStatus` 엔티티 대신 `SessionRegistry`를 활용해 사용자의 로그인 여부를 판단하도록 리팩토링하세요.
- [x] `UserStatus` 엔티티와 관련 코드를 삭제하세요.

---

## 주요 변경사항
- CSR 환경에 맞춘 Spring Security, CSRF, formLogin 기반 인증 흐름 구성
- BCrypt 기반 회원가입 비밀번호 암호화 적용
- RememberMe 기반 로그인 유지 기능 구성
- 세션 기반 현재 사용자 조회 및 로그아웃 API 구성
- 사용자 권한 정의, 권한 수정 API, 어드민 계정 초기화 구현
- 초기 관리자 계정 및 자기 자신 권한 변경 보호 정책 추가
- 권한 변경 보호 정책 위반 시 일반 인가 실패와 구분되도록 `409 USER_409` 응답으로 분리
- URL 및 Method Security 기반 인가 적용
- RoleHierarchy를 통한 `ADMIN > CHANNEL_MANAGER > USER` 권한 계층 구성
- 동일 계정 동시 로그인 제한 및 권한 변경 시 기존 세션 만료 처리
- `UserStatus` 기반 온라인 상태 관리 제거 후 `SessionRegistry` 기반 로그인 상태 판단으로 리팩토링

## 멘토에게
- CSR 환경에서 CSRF 토큰을 쿠키와 헤더로 다룰 때 실무에서 가장 자주 점검하는 보안 포인트가 궁금합니다.
- Spring Security의 기본 formLogin 흐름을 유지하면서 API 응답 형태만 JSON으로 바꿀 때 주의해야 할 설계 기준이 궁금합니다.
- 권한 변경 시 기존 세션을 즉시 만료하는 정책이 사용자 경험과 보안 사이에서 어떤 트레이드오프를 가지는지 궁금합니다.
