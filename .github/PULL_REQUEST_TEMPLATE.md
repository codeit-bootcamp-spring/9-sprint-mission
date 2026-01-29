
## 요구사항

### 기본 요구사항
- [x] 기본 항목 1
#### 프로젝트 초기화
- [x] IntelliJ를 통해 다음의 조건으로 Java 프로젝트를 생성합니다.
- [x]  IntelliJ에서 제공하는 프로젝트 템플릿 중 Java를 선택합니다.
- [x]  프로젝트의 경로는 스프린트 미션 리포지토리의 경로와 같게 설정합니다.
- [x]  Create Git Repository 옵션은 체크하지 않습니다.
- [x]  Build system은 Gradle을 사용합니다. Gradle DSL은 Groovy를 사용합니다.
- [x]  JDK 17을 선택합니다.
- [x]  GroupId는 com.sprint.mission로 설정합니다.
- [x]  ArtifactId는 수정하지 않습니다.
- [x]  .gitignore에 IntelliJ와 관련된 파일이 형상관리 되지 않도록 .idea디렉토리를 추가합니다.
#### 도메인 모델링
- [x] 디스코드 서비스를 활용해보면서 각 도메인 모델에 필요한 정보를 도출하고, Java Class로 구현하세요.
  - [x] 패키지명: com.sprint.mission.discodeit.entity
  - [x] 도메인 모델 정의
    - [x] 공통
      - [x] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언합니다.
      - [x] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언합니다.
    - [x] User
    - [x] Channel
    - [x] Message
  - [x] 생성자
    - [x] id는 생성자에서 초기화하세요.
    - [x] createdAt는 생성자에서 초기화하세요.
    - [x] id, createdAt, updatedAt을 제외한 필드는 생성자의 파라미터를 통해 초기화하세요.
  - [x] 메소드
    - [x] 각 필드를 반환하는 Getter 함수를 정의하세요.
    - [x] 필드를 수정하는 update 함수를 정의하세요.
#### 서비스 설계 및 구현
- [x] 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언하세요.
  - [x] 인터페이스 패키지명: com.sprint.mission.discodeit.service
  - [x] 인터페이스 네이밍 규칙: [도메인 모델 이름]Service
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
  - [x] 클래스 네이밍 규칙: JCF[인터페이스 이름]
  - [x] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
  - [x] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
#### 메인 클래스 구현
- [x] 메인 메소드가 선언된 JavaApplication 클래스를 선언하고, 도메인 별 서비스 구현체를 테스트해보세요.
  - [x] 등록
  - [x] 조회(단건, 다건)
  - [x] 수정
  - [x] 수정된 데이터 조회
  - [x] 삭제
  - [x] 조회를 통해 삭제되었는지 확인
---
- [x] 기본 항목 2 
#### File IO를 통한 데이터 영속화
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.file
  - [x] 클래스 네이밍 규칙: File[인터페이스 이름]
  - [x] JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요.
- [x] Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.

#### 서비스 구현체 분석
- [x] JCF*Service 구현체와 File*Service 구현체를 비교하여 공통점과 차이점을 발견해보세요.
  - [x] "비즈니스 로직"과 관련된 코드를 식별해보세요.
  - [x] "저장 로직"과 관련된 코드를 식별해보세요.

#### 레포지토리 설계 및 구현
- [x] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
  - [x] 인터페이스 패키지명: com.sprint.mission.discodeit.repository
  - [x] 인터페이스 네이밍 규칙: [도메인 모델 이름]Repository
- [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
  - [x] 클래스 패키지명: com.sprint.mission.discodeit.repository.jcf
  - [x] 클래스 네이밍 규칙: JCF[인터페이스 이름]
  - [x] 기존에 구현한 JCF*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
- [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
  - [x] 클래스 패키지명: com.sprint.mission.discodeit.repository.file
  - [x] 클래스 네이밍 규칙: File[인터페이스 이름]
  - [x] 기존에 구현한 File*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
---
### 심화 요구사항
- [x] 심화 항목 1
#### 서비스 간 의존성 주입
- [x] 도메인 모델 간 관계를 고려해서 검증하는 로직을 추가하고, 테스트해보세요
---
- [x] 심화 항목 2
#### 관심사 분리를 통한 레이어 간 의존성 주입
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.basic
  - [x] 클래스 네이밍 규칙: Basic[인터페이스 이름]
  - [x] 기존에 구현한 서비스 구현체의 "비즈니스 로직"과 관련된 코드를 참고하여 구현하세요.
  - [x] 필요한 Repository 인터페이스를 필드로 선언하고 생성자를 통해 초기화하세요.
  - [x] "저장 로직"은 Repository 인터페이스 필드를 활용하세요. (직접 구현하지 마세요.)
- [x] Basic*Service 구현체를 활용하여 테스트해보세요.
- [x]  JCF*Repository  구현체를 활용하여 테스트해보세요.
- [x]  File*Repository 구현체를 활용하여 테스트해보세요.
- [x] 이전에 작성했던 코드(JCF*Service 또는 File*Service)와 비교해 어떤 차이가 있는지 정리해보세요.
---
## 주요 변경사항
- 파일 기반 Repository들의 공통 파일 처리 로직을 AbstractFileRepository로 분리했습니다.
- 이를 통해 하위 Repository 구현체의 중복 코드를 제거하고 책임을 명확히 했습니다.
- FileUserRepository에서 이메일/휴대폰 중복 체크 로직을 실제 파일 기준으로 재구현했습니다.
- PR description 을 통해 기본, 심화 요구사항 세부내역 및 체크현황 수정했습니다.
---
## 스크린샷
![image](이미지url)
---
## 멘토에게
- 파일 저장/로드 로직을 static 유틸이 아닌 AbstractFileRepository로 분리했는데, 이 정도 규모에서 추상 클래스를 도입하는 게 적절한 선택인지 궁금합니다.
- 추후 파일 포맷을 직렬화가 아닌 JSON 등으로 변경한다면 현재 Abstract 구조가 확장에 유리한지 궁금합니다.
- AbstractFileRepository 구조에서 더 단순하거나 명확하게 개선할 수 있는 방향이 있다면 조언 부탁드립니다.