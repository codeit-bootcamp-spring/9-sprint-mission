
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
- 
-
---
## 스크린샷

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

### 기본 요구 사항
#### Spring 프로젝트 초기화
-  [x] Spring Initializr를 통해 zip 파일을 다운로드하세요.
-   [x] 빌드 시스템은 Gradle - Groovy를 사용합니다.
-   [x] 언어는 Java 17를 사용합니다.
-   [x] Spring Boot의 버전은 3.4.0입니다.
-   [x] GroupId는 com.sprint.mission입니다.
-   [x] ArtifactId와 Name은 discodeit입니다.
-   [x] packaging 형식은 Jar입니다
-   [x] Dependency를 추가합니다.
-   [x] Lombok
-   [x] Spring Web
-   [x] zip 파일을 압축해제하고 원래 진행 중이던 프로젝트에 붙여넣기하세요. 일부 파일은 덮어쓰기할 수 있습니다.
-   [x] application.properties 파일을 yaml 형식으로 변경하세요.
-   [x] DiscodeitApplication의 main 메서드를 실행하고 로그를 확인해보세요.
#### Bean 선언 및 테스트
-[x] File*Repository 구현체를 Repository 인터페이스의 Bean으로 등록하세요.
-[x] Basic*Service 구현체를 Service 인터페이스의 Bean으로 등록하세요.
-[x] JavaApplication에서 테스트했던 코드를 DiscodeitApplication에서 테스트해보세요.
-[x]  JavaApplication 의 main 메소드를 제외한 모든 메소드를 DiscodeitApplication클래스로 복사하세요.
-[x]  JavaApplication의 main 메소드에서 Service를 초기화하는 코드를 Spring Context를 활용하여 대체하세요.
-[x]  JavaApplication의 main 메소드의 셋업, 테스트 부분의 코드를 DiscodeitApplication클래스로 복사하세요.
#### Spring 핵심 개념 이해하기
-[x] JavaApplication과 DiscodeitApplication에서 Service를 초기화하는 방식의 차이에 대해 다음의 키워드를 중심으로 정리해보세요.
#### IoC Container
- JavaApplecation : 개발자가 직접 관리한다.
- DiscodeitApplication : Spring IoC Container가 관리한다.
#### Dependency Injection
- JavaApplecation : 개발자가 생성자를 통해 주입한다.
- DiscodeitApplication : 스프링이 자동으로 주입한다.
#### Bean
- JavaApplecation : 스프링이 관리 하지않는다.
- DiscodeitApplication : Spring IoC Container가 관리한다.
#### Lombok 적용
 - [x] 도메인 모델의 getter 메소드를 @Getter로 대체해보세요.
 - [x] Basic*Service의 생성자를 @RequiredArgsConstructor로 대체해보세요.
## 추가 기능 요구사항
#### 시간 타입 변경하기
- [x] 시간을 다루는 필드의 타입은 Instant로 통일합니다.
    - 기존에 사용하던 Long보다 가독성이 뛰어나며, 시간대(Time Zone) 변환과 정밀한 시간 연산이 가능해 확장성이 높습니다.
#### 새로운 도메인 추가하기
- [x]  공통: 앞서 정의한 도메인 모델과 동일하게 공통 필드(id, createdAt, updatedAt)를 포함합니다.

- [x]  ReadStatus

     - 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
- [x]  UserStatus

    - 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
-[x] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
  - 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
-[x]  BinaryContent
    - 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
   -[x] 수정 불가능한 도메인 모델로 간주합니다. 따라서 updatedAt 필드는 정의하지 않습니다.
   -[x] User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.

- [x]  각 도메인 모델 별 레포지토리 인터페이스를 선언하세요.

   - 레포지토리 구현체(File, JCF)는 아직 구현하지 마세요. 이어지는 서비스 고도화 요구사항에 따라 레포지토리 인터페이스에 메소드가 추가될 수 있어요.
     
### UserService 고도화
#### 고도화
- create
- [ ] 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
- [ ] DTO를 활용해 파라미터를 그룹화합니다.
  - 유저를 등록하기 위해 필요한 파라미터, 프로필 이미지를 등록하기 위해 필요한 파라미터 등
- [ ] username과 email은 다른 유저와 같으면 안됩니다.
- [ ] UserStatus를 같이 생성합니다.
  - find, findAll
    - DTO를 활용하여:
    - [ ] 사용자의 온라인 상태 정보를 같이 포함하세요.
    - [ ] 패스워드 정보는 제외하세요.
  - update
     - [ ] 선택적으로 프로필 이미지를 대체할 수 있습니다.
     - [ ] DTO를 활용해 파라미터를 그룹화합니다.
       - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
  - delete
     - [ ] 관련된 도메인도 같이 삭제합니다.
      - BinaryContent(프로필), UserStatus
  - 의존성
     - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.



### AuthService 구현
- login
  - [ ] username, password과 일치하는 유저가 있는지 확인합니다.
  - [ ] 일치하는 유저가 있는 경우: 유저 정보 반환
  - [ ] 일치하는 유저가 없는 경우: 예외 발생
  - [ ] DTO를 활용해 파라미터를 그룹화합니다.
- 의존성
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.



### ChannelService 고도화
- 고도화
    - create
        - PRIVATE 채널과 PUBLIC 채널을 생성하는 메소드를 분리합니다.
        - [ ] 분리된 각각의 메소드를 DTO를 활용해 파라미터를 그룹화합니다.
        - PRIVATE 채널을 생성할 때:
          - [ ] 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
          - [ ] name과 description 속성은 생략합니다.
        - PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
    - find
        - DTO를 활용하여:
          - [ ] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
          - [ ] PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
    - findAll
        - DTO를 활용하여:
        - [ ] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
        - [ ] PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
        - [ ] 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
        - [ ] PUBLIC 채널 목록은 전체 조회합니다.
        - [ ] PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.
    - update
        - [ ] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
        - [ ] PRIVATE 채널은 수정할 수 없습니다.
    - delete
       -  [ ] 관련된 도메인도 같이 삭제합니다.
        - Message, ReadStatus
    - 의존성
       - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.



### MessageService 고도화
- 고도화
    - create
      - [ ] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
      - [ ] DTO를 활용해 파라미터를 그룹화합니다.
    - findAll
      - [ ] 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findallByChannelId
    - update
      - [ ] DTO를 활용해 파라미터를 그룹화합니다.
      - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - delete
      - [ ] 관련된 도메인도 같이 삭제합니다.
      첨부파일(BinaryContent)
    - 의존성
      - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.



### ReadStatusService 구현
- create
    - [ ] DTO를 활용해 파라미터를 그룹화합니다.
    - [ ] 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
      - [ ] 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- find
  - [ ] id로 조회합니다.
  - findAllByUserId
        - [ ] userId를 조건으로 조회합니다.
  - update
        - [ ] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
        - delete
  - [ ] id로 삭제합니다.
  - 의존성
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.



### UserStatusService 고도화
- create
- [ ] DTO를 활용해 파라미터를 그룹화합니다.
- [ ] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
- [ ] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- find
- [ ] id로 조회합니다.
- findAll
- [ ] 모든 객체를 조회합니다.
- update
- [ ] DTO를 활용해 파라미터를 그룹화합니다.
- 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- updateByUserId
- [ ] userId 로 특정 User의 객체를 업데이트합니다.
- delete
- [ ] id로 삭제합니다.
  - 의존성
  -같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.



BinaryContentService 구현
create
[ ] DTO를 활용해 파라미터를 그룹화합니다.
find
[ ] id로 조회합니다.
findAllByIdIn
[ ] id 목록으로 조회합니다.
delete[build.gradle](../../../../Downloads/sprint_mission_4_base/build.gradle)
[gradle](../../../../Downloads/sprint_mission_4_base/gradle)
[gradlew](../../../../Downloads/sprint_mission_4_base/gradlew)
[gradlew.bat](../../../../Downloads/sprint_mission_4_base/gradlew.bat)
[HELP.md](../../../../Downloads/sprint_mission_4_base/HELP.md)
[README.md](../../../../Downloads/sprint_mission_4_base/README.md)
[settings.gradle](../../../../Downloads/sprint_mission_4_base/settings.gradle)
[src](../../../../Downloads/sprint_mission_4_base/src)
[ ] id로 삭제합니다.
의존성
같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

#### Entity를 Controller까지 노출했을 때 발생하는 문제점
1. Entity와 API 스펙의 강한 결합 (Coupling)

 - 엔티티는 데이터베이스 스키마와 직접적으로 연결된 핵심 비즈니스 도메인입니다.

 - 엔티티를 API 응답으로 직접 반환하면, DB 컬럼명이나 내부 로직이 변경될 때 API 스펙이 예고 없이 함께 변경됩니다. 이는 API를 호출하는 프론트엔드(클라이언트)에 예상치 못한 장애를 유발합니다.

2. 양방향 연관관계 시 무한 순환 참조 문제

 - JPA 엔티티 간에 양방향 연관관계(예: User ↔ UserStatus)가 설정되어 있을 때, Jackson 라이브러리가 엔티티를 JSON으로 직렬화하는 과정에서 서로를 끊임없이 참조하게 됩니다.

 - 결과적으로 StackOverflowError가 발생하여 서버가 다운될 위험이 있습니다.

3. 민감한 데이터의 무분별한 노출

 - 엔티티에는 회원의 비밀번호, 내부 관리용 ID, 생성/수정일 등 클라이언트에게 숨겨야 할 데이터가 모두 포함되어 있습니다.

 - 이를 막기 위해 엔티티 클래스 내부에 @JsonIgnore 같은 프레젠테이션 계층 전용 어노테이션을 추가하게 되면, 순수해야 할 엔티티가 화면 출력 로직으로 오염됩니다.

4. OSIV(Open Session In View) 비활성화 환경에서의 예외 발생

 - 프로덕션 환경에서는 데이터베이스 커넥션 고갈을 막고 성능을 최적화하기 위해 보통 spring.jpa.open-in-view = false로 설정합니다.

 - OSIV가 꺼져 있으면 트랜잭션이 서비스 계층에서 종료됩니다. 만약 지연 로딩(Lazy Loading)으로 설정된 엔티티 필드를 컨트롤러(트랜잭션 외부)에서 JSON 직렬화하려고 접근하면 LazyInitializationException 에러가 발생합니다.

#### DTO 도입으로 얻게 된 이점
 - API 스펙의 안정성 보장: 엔티티 내부 구현이 변경되더라도, 컨트롤러에서는 변하지 않는 DTO 스펙으로 매핑하여 반환하므로 클라이언트와의 계약(Contract)을 안전하게 유지할 수 있습니다.

 - 화면(Client)에 최적화된 데이터 전송: API를 요청하는 화면의 용도에 맞춰 필요한 데이터만 골라서 조립할 수 있으므로, 네트워크 페이로드 크기를 줄이고 통신 효율을 높일 수 있습니다.

 - 관심사의 분리(Separation of Concerns): @NotBlank, @Email 등의 API 검증(Validation) 로직을 DTO가 전담하게 함으로써, 엔티티는 순수한 비즈니스 로직과 데이터베이스 매핑에만 집중할 수 있게 되었습니다.



## 주요 변경사항
-
-
---
## 스크린샷
![스크린샷 2026-03-15 오후 5.33.19.png](../../../../Desktop/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-15%20%EC%98%A4%ED%9B%84%205.33.19.png)


---
## 멘토에게
- 
-
