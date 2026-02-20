
## 요구사항

### 기본 요구사항

#### 컨트롤러 레이어 구현
- [x] DiscodeitApplication의 테스트 로직은 삭제하세요.
- [x] 지금까지 구현한 서비스 로직을 활용해 웹 API를 구현하세요.  
  - 이때 @RequestMapping만 사용해 구현해보세요.

### 웹 API 요구사항
- [x] 웹 API의 예외를 전역으로 처리하세요.
#### 사용자 관리
- [x] 사용자를 등록할 수 있다.
- [x] 사용자 정보를 수정할 수 있다.
- [x] 사용자를 삭제할 수 있다.
- [x] 모든 사용자를 조회할 수 있다.
- [x] 사용자의 온라인 상태를 업데이트할 수 있다.
#### 권한 관리
- [x] 사용자는 로그인할 수 있다.
#### 채널 관리
- [x] 공개 채널을 생성할 수 있다.
- [x] 비공개 채널을 생성할 수 있다.
- [x] 공개 채널의 정보를 수정할 수 있다.
- [x] 채널을 삭제할 수 있다.
- [x] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
#### 메시지 관리
- [x] 메시지를 보낼 수 있다.
- [x] 메시지를 수정할 수 있다.
- [x] 메시지를 삭제할 수 있다.
- [x] 특정 채널의 메시지 목록을 조회할 수 있다.
#### 메시지 수신 정보 관리
- [x] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
- [x] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
- [x] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
#### 바이너리 파일 다운로드
- [x] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
### API 테스트
- [x] Postman을 활용해 컨트롤러를 테스트 하세요.
  - Postman API 테스트 결과를 다음과 같이 export하여 PR에 첨부해주세요.


## 심화 요구사항
#### 정적 리소스 서빙
- [x] 사용자 목록 조회, BinaryContent 파일 조회 API를 다음의 조건을 만족하도록 수정하세요.
  - [x] 사용자 목록 조회
    - url: /api/user/findAll
    - 요청
      - 파라미터, 바디 없음
    - 응답
      - ResponseEntity<List<UserDto>>
  - [x] BinaryContent 파일 조회 
    - url: /api/binaryContent/find
    - 요청
      - 파라미터: binaryContentId
      - 바디 없음
    - 응답: ResponseEntity<BinaryContent>
  - [x]  다음의 파일을 활용하여 사용자 목록을 보여주는 화면을 서빙해보세요.
#### 생성형 AI 활용
  - 생성형 AI (Claude, ChatGPT 등)를 활용해서 위 이미지와 비슷한 화면을 생성 후 서빙해보세요.
---
## 주요 변경사항
- 정적 리소스 서빙을 위해 resources/static에 index.html/script.js/styles.css를 배치하고 사용자 목록 화면을 제공했습니다.
- 심화 API 요구사항에 맞춰 /api/user/findAll, /api/binaryContent/find 엔드포인트를 추가/연결했습니다.
- UserView 시그니처 변경(createdAt/updatedAt 포함)에 맞춰 UserService/AuthService 반환 매핑을 정리했습니다.
- 기본 프로필/파비콘 리소스(default-avatar.png, favicon.ico)를 추가해 화면 fallback 및 로그 노이즈를 줄였습니다.
---
## 스크린샷
![image](이미지url)
---
## 멘토에게
- 정적 리소스를 resources 루트에 두었다가 static 폴더로 옮겨야 정상 서빙되는 것을 확인했습니다.
  Spring Boot에서 정적 리소스 위치를 명확히 분리하는 이유와, 실무에서는 static과 API 서버를 함께 두는 구조를 얼마나 유지하는지 궁금합니다.
- BinaryContent 조회 API에서 Base64를 그대로 반환하도록 구현했는데,
  실무에서는 이렇게 JSON으로 Base64를 내려주는 방식이 일반적인지,
  아니면 파일 스트리밍 방식(ResponseEntity 등)이 더 권장되는지 궁금합니다.