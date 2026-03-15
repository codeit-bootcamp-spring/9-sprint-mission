
## 요구사항

### 기본 요구사항

#### 데이터베이스
- [x] 아래와 같이 데이터베이스 환경을 설정하세요.
  - 데이터베이스: discodeit
  - 유저: discodeit_user
  - 패스워드: discodeit1234
- [x] ERD를 참고하여 DDL을 작성하고, 테이블을 생성하세요.
  - 작성한 DDL 파일은 /src/main/resources/schema.sql 경로에 포함하세요.
    - PK: Primary Key
    - UK: Unique Key
    - NN: Not Null
    - FK: Foreign Key
    - ON DELETE CASCADE: 연관 엔티티 삭제 시 같이 삭제
    - ON DELETE SET NULL: 연관 엔티티 삭제 시 NULL로 변경
  
#### Spring Data JPA 적용하기
- [x] Spring Data JPA와 PostgreSQL을 위한 의존성을 추가하세요.
- [x] 앞서 구성한 데이터베이스에 연결하기 위한 설정값을 application.yaml 파일에 작성하세요.
- [x] 디버깅을 위해 SQL 로그와 관련된 설정값을 application.yaml 파일에 작성하세요.

#### 엔티티 정의하기
- [x] 클래스 다이어그램을 참고해 도메인 모델의 공통 속성을 추상 클래스로 정의하고 상속 관계를 구현하세요.
- [x] JPA의 어노테이션을 활용해 createdAt, updatedAt 속성이 자동으로 설정되도록 구현하세요.
  - @CreatedDate, @LastModifiedDate
- [x] 클래스 다이어그램을 참고해 클래스 참조 관계를 수정하세요. 필요한 경우 생성자, update 메소드를 수정할 수 있습니다.
  - 단, 아직 JPA Entity와 관련된 어노테이션은 작성하지 마세요.
- [x] ERD와 클래스 다이어그램을 토대로 연관관계 매핑 정보를 표로 정리해보세요.

  | 엔티티 관계 | 다중성 | 방향성 | 부모-자식 관계 | 연관관계의 주인 |
  |---|---|---|---|---|
  | User : UserStatus | 1:1 | User → UserStatus 양방향 | 부모: User / 자식: UserStatus | UserStatus |
  | User : BinaryContent(profile) | 1:1 | User → BinaryContent 단방향 | 부모: User / 자식: BinaryContent | User |
  | User : Message(author) | 1:N | Message → User 단방향 | 부모: User / 자식: Message | Message |
  | Channel : Message | 1:N | Message → Channel 단방향 | 부모: Channel / 자식: Message | Message |
  | User : ReadStatus | 1:N | ReadStatus → User 단방향 | 부모: User / 자식: ReadStatus | ReadStatus |
  | Channel : ReadStatus | 1:N | ReadStatus → Channel 단방향 | 부모: Channel / 자식: ReadStatus | ReadStatus |
  | Message : BinaryContent(attachment) | N:M | Message → BinaryContent 단방향 | 연결 엔티티(MessageAttachment)를 통한 관계 | MessageAttachment |
  | Message : MessageAttachment | 1:N | Message → MessageAttachment 양방향 가능 | 부모: Message / 자식: MessageAttachment | MessageAttachment |
  | BinaryContent : MessageAttachment | 1:N | MessageAttachment → BinaryContent 단방향 | 부모: BinaryContent / 자식: MessageAttachment | MessageAttachment |

- [x] JPA 주요 어노테이션을 활용해 ERD, 연관관계 매핑 정보를 도메인 모델에 반영해보세요.
  - @Entity, @Table
  - @Column, @Enumerated
  - @OneToMany, @OneToOne, @ManyToOne
  - @JoinColumn, @JoinTable
- [x] ERD의 외래키 제약 조건과 연관관계 매핑 정보의 부모-자식 관계를 고려해 영속성 전이와 고아 객체를 정의하세요.
    - cascade, orphanRemoval
  
#### 레포지토리와 서비스에 JPA 도입하기
- [x] 기존의 Repository 인터페이스를 JPARepository로 정의하고 쿼리메소드로 대체하세요.
  - FileRepository와 JCFRepository 구현체는 삭제합니다.
- [x] 영속성 컨텍스트의 특징에 맞추어 서비스 레이어를 수정해보세요.
  - 힌트: 트랜잭션, 영속성 전이, 변경 감지, 지연로딩

#### DTO 적극 도입하기
- [x] Entity를 Controller 까지 그대로 노출했을 때 발생할 수 있는 문제점에 대해 정리해보세요.
  - Entity를 Controller까지 그대로 노출하면 다음과 같은 문제가 발생할 수 있습니다.
    - Entity와 API 응답이 강하게 결합되어 도메인 구조 변경이 API 스펙 변경으로 이어질 수 있습니다.
    - OSIV를 비활성화한 환경에서는 지연 로딩된 연관 객체를 직렬화하는 과정에서 예외가 발생할 수 있습니다.
    - 양방향 연관관계가 있는 경우 JSON 직렬화 시 순환 참조 문제가 발생할 수 있습니다.
    - password 같은 민감한 데이터가 의도치 않게 노출될 수 있습니다.
    - 클라이언트가 필요로 하는 응답 형태와 Entity 구조가 다를 수 있어 응답 전용 모델이 필요합니다.
    - DTO를 도입하면 API 응답 형식을 명확하게 통제할 수 있고, 필요한 데이터만 선택적으로 노출할 수 있으며,
    - Entity와 API 사이의 결합도를 낮출 수 있습니다.
- [x] 다음의 클래스 다이어그램을 참고하여 DTO를 정의하세요.
- [x] Entity를 DTO로 매핑하는 로직을 책임지는 Mapper 컴포넌트를 정의해 반복되는 코드를 줄여보세요.

#### BinaryContent 저장 로직 고도화
- [x] BinaryContent 엔티티는 파일의 메타 정보(fileName, size, contentType)만 표현하도록 bytes 속성을 제거하세요.
- [x] BinaryContent의 byte[] 데이터 저장을 담당하는 인터페이스를 설계하세요.
  - 저장 매체의 확장성(로컬 저장소, 원격 저장소)을 고려해 인터페이스부터 설계합니다.
- [x] 서비스 레이어에서 기존에 BinaryContent를 저장하던 로직을 BinaryContentStorage를 활용하도록 리팩토링하세요.
- [x] BinaryContentController에 파일을 다운로드하는 API를 추가하고, BinaryContentStorage에 로직을 위임하세요.
- [x] 로컬 디스크 저장 방식으로 BinaryContentStorage 구현체를 구현하세요.
- [x] discodeit.storage.type 값이 local 인 경우에만 Bean으로 등록되어야 합니다.

#### 페이징과 정렬
- [x] 메시지 목록을 조회할 때 다음의 조건에 따라 페이지네이션 처리를 해보세요.
  - 50개씩 최근 메시지 순으로 조회합니다.
  - 총 메시지가 몇개인지 알 필요는 없습니다.
- [x] 일관된 페이지네이션 응답을 위해 제네릭을 활용해 DTO로 구현하세요.
- [x] Slice 또는 Page 객체로부터 DTO를 생성하는 Mapper를 구현하세요.

---

### 심화 요구사항

#### N+1 문제
- [x] N+1 문제가 발생하는 쿼리를 찾고 해결해보세요.
읽기전용 트랜잭션 활용
- [x] 프로덕션 환경에서는 OSIV를 비활성화하는 경우가 많습니다. 
- 이때 서비스 레이어의 조회 메소드에서 발생할 수 있는 문제를 식별하고, 읽기 전용 트랜잭션을 활용해 문제를 해결해보세요.

#### OSIV 비활성화하기
```
spring:
jpa:
open-in-view: false
```

#### 페이지네이션 최적화
- [x] 오프셋 페이지네이션과 커서 페이지네이션 방식의 차이에 대해 정리해보세요.
  - 오프셋 페이지네이션은 OFFSET, LIMIT으로 페이지 번호 기준 조회를 하는 방식이다.
  - 커서 페이지네이션은 마지막 조회 데이터의 기준값을 이용해 다음 데이터를 조회하는 방식이다.
  - 오프셋 방식은 구현이 쉽고 페이지 이동이 편하지만, 뒤로 갈수록 성능이 떨어지고 데이터 변경 시 중복이나 누락이 생길 수 있다.
  - 커서 방식은 성능이 더 안정적이고 데이터 일관성이 좋지만, 임의 페이지로 바로 이동하기 어렵고 구현이 더 복잡하다.
  - 즉, 오프셋은 일반적인 페이지 UI에 적합하고, 커서는 무한 스크롤이나 대량 데이터 조회에 더 적합하다.
- [x] 기존에 구현한 오프셋 페이지네이션을 커서 페이지네이션으로 리팩토링하세요.

#### 다음의 API 명세를 준수하세요.
- API 스펙 v1.2
- API 스펙을 준수한다면, 아래의 프론트엔드 코드와 호환됩니다.
- 정적 리소스 v1.2.4
- 소스 코드(참고용) v1.2.4

#### MapStruct 적용
- [x] Entity와 DTO를 매핑하는 보일러플레이트 코드를 MapStruct 라이브러리를 활용해 간소화해보세요.

---
## 주요 변경사항
- 스키마 DDL 작성 및 테이블 생성
- Spring Data JPA와 PostgreSQL 의존성 추가 및 설정
- 도메인 모델에 JPA 어노테이션 적용
- Repository 인터페이스를 JPARepository로 정의 및 쿼리메소드로 대체
- Entity를 DTO로 매핑하는 Mapper 컴포넌트 정의
- BinaryContent 저장 로직 고도화 및 API 추가
- 메시지 목록 조회 시 페이지네이션 처리
- N+1 문제 해결 및 OSIV 비활성화 대응

---
## 멘토에게
- 코드 리뷰 통해 질문 이어나가겠습니다. 감사합니다!
