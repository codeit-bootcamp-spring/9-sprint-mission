# Discodeit 프로젝트 - 초보자를 위한 완전 가이드

> Spring Boot 3 기반 Discord 클론 프로젝트의 구조와 코드를 초보자 관점에서 상세히 설명합니다.

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [패키지 구조](#2-패키지-구조)
3. [계층형 아키텍처 이해하기](#3-계층형-아키텍처-이해하기)
4. [Entity 계층 - 데이터의 뼈대](#4-entity-계층---데이터의-뼈대)
5. [DTO 계층 - 데이터 전달 객체](#5-dto-계층---데이터-전달-객체)
6. [Repository 계층 - 데이터 저장소](#6-repository-계층---데이터-저장소)
7. [Service 계층 - 비즈니스 로직](#7-service-계층---비즈니스-로직)
8. [전체 데이터 흐름 예시](#8-전체-데이터-흐름-예시)
9. [직접 구현해보기](#9-직접-구현해보기)
10. [자주 하는 실수와 해결법](#10-자주-하는-실수와-해결법)

---

## 1. 프로젝트 개요

### 1.1 이 프로젝트는 무엇인가요?

**Discodeit**은 Discord와 유사한 채팅 애플리케이션의 백엔드입니다.

주요 기능:
- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **채널 관리**: 공개/비공개 채널 생성 및 관리
- **메시지**: 채널 내 메시지 송수신
- **파일 첨부**: 프로필 이미지, 메시지 첨부파일
- **상태 관리**: 온라인/오프라인 상태, 메시지 읽음 상태

### 1.2 사용 기술

| 기술 | 용도 |
|------|------|
| Java 17+ | 프로그래밍 언어 |
| Spring Boot 3 | 웹 프레임워크 |
| Lombok | 보일러플레이트 코드 자동 생성 |
| UUID | 고유 식별자 생성 |

---

## 2. 패키지 구조

```
src/main/java/com/sprint/mission/discodeit/
│
├── DiscodeitApplication.java    # 애플리케이션 시작점
│
├── entity/                      # 📦 엔티티 (데이터 모델)
│   ├── BaseEntity.java         # 모든 엔티티의 부모 클래스
│   ├── User.java               # 사용자
│   ├── Channel.java            # 채널
│   ├── Message.java            # 메시지
│   ├── BinaryContent.java      # 파일 (이미지, 첨부파일)
│   ├── UserStatus.java         # 사용자 온라인 상태
│   ├── ReadStatus.java         # 메시지 읽음 상태
│   └── ChannelType.java        # 채널 타입 (PUBLIC/PRIVATE)
│
├── dto/                         # 📨 데이터 전송 객체
│   ├── request/                # 요청용 DTO
│   │   ├── UserCreateRequest.java
│   │   ├── UserUpdateRequest.java
│   │   └── ...
│   └── response/               # 응답용 DTO
│       ├── UserResponse.java
│       └── ...
│
├── repository/                  # 💾 저장소 (데이터 접근)
│   ├── UserRepository.java     # 인터페이스
│   ├── file/                   # 파일 기반 구현체
│   │   └── FileUserRepository.java
│   └── jcf/                    # 메모리 기반 구현체
│       └── JCFUserRepository.java
│
└── service/                     # ⚙️ 서비스 (비즈니스 로직)
    ├── UserService.java        # 인터페이스
    └── basic/                  # 기본 구현체
        └── BasicUserService.java
```

### 왜 이렇게 나눌까요?

각 패키지는 **하나의 역할**만 담당합니다:
- **entity**: "데이터가 어떻게 생겼는지" 정의
- **dto**: "어떤 데이터를 주고받을지" 정의
- **repository**: "데이터를 어떻게 저장/조회할지" 정의
- **service**: "어떤 작업을 수행할지" 정의

이렇게 나누면 **수정이 쉽고**, **테스트가 쉽고**, **이해하기 쉽습니다**.

---

## 3. 계층형 아키텍처 이해하기

### 3.1 계층 구조 다이어그램

```
┌─────────────────────────────────────────────────────────┐
│                    클라이언트 (Frontend)                  │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼ HTTP 요청/응답
┌─────────────────────────────────────────────────────────┐
│                  Controller 계층 (REST API)              │
│            - HTTP 요청을 받아서 Service에 전달             │
│            - Service 결과를 HTTP 응답으로 반환             │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼ DTO 전달
┌─────────────────────────────────────────────────────────┐
│                    Service 계층                          │
│            - 비즈니스 로직 처리                            │
│            - 여러 Repository를 조합하여 작업 수행           │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼ Entity 전달
┌─────────────────────────────────────────────────────────┐
│                   Repository 계층                        │
│            - 데이터 저장/조회/수정/삭제                     │
│            - 파일 또는 메모리에 저장                        │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Entity 계층                           │
│            - 데이터의 구조 정의                            │
└─────────────────────────────────────────────────────────┘
```

### 3.2 데이터 흐름 방향

```
요청 시: 클라이언트 → Controller → Service → Repository → 저장소
응답 시: 저장소 → Repository → Service → Controller → 클라이언트
```

**핵심 규칙**:
- 상위 계층은 하위 계층을 호출할 수 있음
- 하위 계층은 상위 계층을 몰라야 함 (의존성 방향 통일)

---

## 4. Entity 계층 - 데이터의 뼈대

### 4.1 BaseEntity - 모든 엔티티의 부모

모든 엔티티가 공통으로 가지는 속성을 정의합니다.

```java
@Getter
public class BaseEntity implements Serializable {

    protected UUID id;           // 고유 식별자
    protected Instant createdAt; // 생성 시간
    protected Instant updatedAt; // 수정 시간

    public BaseEntity() {
        this.id = UUID.randomUUID();  // 자동으로 고유 ID 생성
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 수정 시 호출하여 수정 시간 갱신
    protected void updateTimeStamp() {
        this.updatedAt = Instant.now();
    }
}
```

**왜 BaseEntity를 만들까요?**
- 모든 데이터는 `id`, `createdAt`, `updatedAt`이 필요함
- 중복 코드를 줄이고 일관성 유지
- 상속을 통해 자동으로 공통 기능 획득

### 4.2 User 엔티티

```java
@Getter
public class User extends BaseEntity {

    private String username;    // 사용자명 (로그인 ID)
    private String email;       // 이메일
    private String password;    // 비밀번호
    private UUID profileId;     // 프로필 이미지 ID (선택)

    // 생성자: 새 사용자 만들 때
    public User(String username, String email, String password, UUID profileId) {
        super();  // BaseEntity 생성자 호출 → id, createdAt, updatedAt 자동 설정
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    // 수정 메서드: null이 아닌 값만 업데이트
    public void update(String username, String email, String password, UUID profileId) {
        if (username != null) this.username = username;
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (profileId != null) this.profileId = profileId;
        updateTimeStamp();  // 수정 시간 갱신
    }
}
```

**포인트 해설**:
1. `extends BaseEntity`: 공통 속성 상속
2. `super()`: 부모 생성자 호출로 id, 시간 자동 생성
3. `update()`: null 체크로 부분 업데이트 가능
4. `updateTimeStamp()`: 수정 시 자동으로 시간 갱신

### 4.3 전체 엔티티 관계도

```
┌──────────────┐
│  BaseEntity  │  ← 모든 엔티티의 부모
└──────────────┘
       △
       │ 상속
       │
┌──────┴──────┬──────────────┬──────────────┬──────────────┐
│             │              │              │              │
▼             ▼              ▼              ▼              ▼
┌──────┐   ┌──────┐   ┌─────────┐   ┌──────────┐   ┌───────────┐
│ User │   │Channel│   │ Message │   │ReadStatus│   │UserStatus │
└──────┘   └──────┘   └─────────┘   └──────────┘   └───────────┘
    │          │           │             │               │
    │          │           │             │               │
    ▼          ▼           ▼             │               │
┌──────────────────────────────────┐     │               │
│        BinaryContent             │     │               │
│   (프로필 이미지, 첨부파일)        │     │               │
└──────────────────────────────────┘     │               │
                                         │               │
                                    사용자-채널        사용자
                                    읽음 상태         온라인 상태
```

### 4.4 각 엔티티 요약

| 엔티티 | 역할 | 주요 필드 |
|--------|------|----------|
| **User** | 사용자 정보 | username, email, password, profileId |
| **Channel** | 채팅방 | type(PUBLIC/PRIVATE), name, description |
| **Message** | 메시지 | content, channelId, authorId, attachmentIds |
| **BinaryContent** | 파일 저장 | fileName, contentType, data(byte[]) |
| **UserStatus** | 온라인 상태 | userId, lastActiveAt |
| **ReadStatus** | 읽음 상태 | userId, channelId, lastReadAt |

---

## 5. DTO 계층 - 데이터 전달 객체

### 5.1 DTO란?

**DTO (Data Transfer Object)**: 계층 간 데이터를 전달하기 위한 객체

**왜 Entity를 직접 안 쓰고 DTO를 쓸까요?**

1. **보안**: 비밀번호 같은 민감한 정보 숨기기
2. **유연성**: 클라이언트가 필요한 형태로 데이터 가공
3. **분리**: Entity 변경이 API에 영향 주지 않음

### 5.2 Request DTO - 요청용

클라이언트가 서버로 데이터를 보낼 때 사용합니다.

```java
// 회원가입 요청 DTO
public record UserCreateRequest(
    String username,   // 사용자명
    String email,      // 이메일
    String password    // 비밀번호
) {}
```

**record란?**
- Java 16+에서 추가된 불변 데이터 클래스
- 자동으로 생성자, getter, equals, hashCode, toString 생성
- DTO에 적합!

**전체 Request DTO 목록**:

| DTO | 용도 | 필드 |
|-----|------|------|
| `UserCreateRequest` | 회원가입 | username, email, password |
| `UserUpdateRequest` | 정보 수정 | username, email, password (모두 선택) |
| `LoginRequest` | 로그인 | username, password |
| `PublicChannelCreateRequest` | 공개 채널 생성 | name, description |
| `PrivateChannelCreateRequest` | 비공개 채널 생성 | memberIds (List) |
| `ChannelUpdateRequest` | 채널 수정 | name, description |
| `MessageCreateRequest` | 메시지 작성 | content, channelId, authorId |
| `MessageUpdateRequest` | 메시지 수정 | content |
| `BinaryContentCreateRequest` | 파일 업로드 | fileName, contentType, data |

### 5.3 Response DTO - 응답용

서버가 클라이언트로 데이터를 보낼 때 사용합니다.

```java
// 사용자 응답 DTO
public record UserResponse(
    UUID id,              // 사용자 ID
    String username,      // 사용자명
    String email,         // 이메일
    UUID profileId,       // 프로필 이미지 ID
    boolean isOnline,     // 온라인 여부 ★ 추가 정보!
    Instant createdAt,    // 생성 시간
    Instant updatedAt     // 수정 시간
) {}
```

**Entity vs Response DTO 비교**:

| 항목 | User Entity | UserResponse DTO |
|------|-------------|------------------|
| password | ✅ 있음 | ❌ 없음 (보안) |
| isOnline | ❌ 없음 | ✅ 있음 (추가 정보) |

---

## 6. Repository 계층 - 데이터 저장소

### 6.1 Repository 패턴이란?

데이터 저장/조회 로직을 **추상화**하여 Service가 저장 방식을 몰라도 되게 합니다.

```
Service: "User 저장해줘"
    ↓
Repository 인터페이스: save(User user)
    ↓
구현체 선택:
    ├─ FileRepository → 파일에 저장
    └─ JCFRepository → 메모리에 저장
```

### 6.2 Repository 인터페이스

```java
public interface UserRepository {

    // 기본 CRUD
    User save(User user);              // 저장/수정
    Optional<User> findById(UUID id);  // ID로 조회
    List<User> findAll();              // 전체 조회
    void deleteById(UUID id);          // 삭제

    // 조회 메서드
    Optional<User> findByUsername(String username);  // 로그인용
    Optional<User> findByEmail(String email);        // 이메일 조회

    // 존재 여부 확인
    boolean existsById(UUID id);
    boolean existsByUsername(String username);  // 중복 체크용
    boolean existsByEmail(String email);        // 중복 체크용
}
```

**Optional이란?**
- 값이 있을 수도, 없을 수도 있는 컨테이너
- null 대신 사용하여 NullPointerException 방지

```java
// Optional 사용 예시
Optional<User> userOpt = userRepository.findById(id);

// 방법 1: 있으면 가져오고, 없으면 예외
User user = userOpt.orElseThrow(() ->
    new NoSuchElementException("User not found"));

// 방법 2: 있으면 가져오고, 없으면 기본값
User user = userOpt.orElse(defaultUser);

// 방법 3: 있을 때만 처리
userOpt.ifPresent(user -> System.out.println(user.getUsername()));
```

### 6.3 구현체 선택 방식

**application.properties에서 설정**:

```properties
# 메모리 저장 (기본값, 테스트/개발용)
discodeit.repository.type=jcf

# 파일 저장 (데이터 영구 보존)
discodeit.repository.type=file
discodeit.repository.file-directory=./data
```

**조건부 빈 등록**:

```java
// 메모리 기반 구현체
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type",
                       havingValue = "jcf",
                       matchIfMissing = true)  // 설정 없으면 기본값
public class JCFUserRepository implements UserRepository { ... }

// 파일 기반 구현체
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type",
                       havingValue = "file")
public class FileUserRepository implements UserRepository { ... }
```

### 6.4 전체 Repository 목록

| Repository | 담당 Entity | 특수 메서드 |
|------------|-------------|-------------|
| `UserRepository` | User | findByUsername, findByEmail |
| `ChannelRepository` | Channel | - |
| `MessageRepository` | Message | findAllByChannelId, deleteAllByChannelId |
| `BinaryContentRepository` | BinaryContent | findAllByIdIn |
| `UserStatusRepository` | UserStatus | findByUserId, deleteByUserId |
| `ReadStatusRepository` | ReadStatus | findByUserIdAndChannelId |

---

## 7. Service 계층 - 비즈니스 로직

### 7.1 Service의 역할

- **비즈니스 규칙 적용**: 중복 체크, 유효성 검증
- **여러 Repository 조합**: 복잡한 작업 처리
- **DTO 변환**: Entity ↔ DTO 변환

### 7.2 BasicUserService 상세 분석

```java
@Service
@RequiredArgsConstructor  // final 필드 생성자 자동 생성
public class BasicUserService implements UserService {

    // 의존성 주입 (생성자 주입)
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
```

**@RequiredArgsConstructor란?**
```java
// Lombok이 자동으로 생성하는 코드:
public BasicUserService(
    UserRepository userRepository,
    BinaryContentRepository binaryContentRepository,
    UserStatusRepository userStatusRepository
) {
    this.userRepository = userRepository;
    this.binaryContentRepository = binaryContentRepository;
    this.userStatusRepository = userStatusRepository;
}
```

### 7.3 회원가입 로직 분석

```java
@Override
public UserResponse create(UserCreateRequest request,
                           BinaryContentCreateRequest profileRequest) {

    // ========== 1단계: 유효성 검증 ==========
    // 사용자명 중복 체크
    if (userRepository.existsByUsername(request.username())) {
        throw new IllegalArgumentException(
            "이 사용자 이름은 이미 존재해요!: " + request.username());
    }

    // 이메일 중복 체크
    if (userRepository.existsByEmail(request.email())) {
        throw new IllegalArgumentException(
            "이 이메일은 이미 존재해요!: " + request.email());
    }

    // ========== 2단계: 프로필 이미지 처리 (선택) ==========
    UUID profileId = null;
    if (profileRequest != null) {
        BinaryContent profile = new BinaryContent(
            profileRequest.fileName(),
            profileRequest.contentType(),
            profileRequest.data()
        );
        profileId = binaryContentRepository.save(profile).getId();
    }

    // ========== 3단계: User 생성 및 저장 ==========
    User user = new User(
        request.username(),
        request.email(),
        request.password(),
        profileId
    );
    User savedUser = userRepository.save(user);

    // ========== 4단계: UserStatus 자동 생성 ==========
    UserStatus userStatus = new UserStatus(savedUser.getId(), Instant.now());
    userStatusRepository.save(userStatus);

    // ========== 5단계: 응답 반환 ==========
    return toUserResponse(savedUser, true);  // 방금 가입 = 온라인
}
```

**흐름 요약**:
```
요청 → 중복검증 → 프로필저장 → User저장 → UserStatus저장 → 응답
```

### 7.4 수정 로직 분석

```java
@Override
public UserResponse update(UUID id, UserUpdateRequest request,
                           BinaryContentCreateRequest profileRequest) {

    // 1. 사용자 찾기
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

    // 2. 프로필 이미지 교체 (선택)
    UUID newProfileId = user.getProfileId();
    if (profileRequest != null) {
        // 기존 이미지가 있으면 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        // 새 이미지 저장
        BinaryContent profile = new BinaryContent(
            profileRequest.fileName(),
            profileRequest.contentType(),
            profileRequest.data()
        );
        newProfileId = binaryContentRepository.save(profile).getId();
    }

    // 3. 사용자 정보 업데이트
    user.update(request.username(), request.email(),
                request.password(), newProfileId);

    // 4. 저장 및 반환
    User savedUser = userRepository.save(user);
    return toUserResponse(savedUser, getOnlineStatus(savedUser.getId()));
}
```

### 7.5 삭제 로직 분석

```java
@Override
public void delete(UUID id) {
    // 1. 사용자 찾기
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

    // 2. 연관 데이터 먼저 삭제 (순서 중요!)
    if (user.getProfileId() != null) {
        binaryContentRepository.deleteById(user.getProfileId());
    }
    userStatusRepository.deleteByUserId(id);

    // 3. 마지막으로 사용자 삭제
    userRepository.deleteById(id);
}
```

**삭제 순서가 중요한 이유**:
- 프로필 이미지, UserStatus → User 순으로 삭제
- 반대로 하면 User 삭제 후 연관 데이터가 고아(orphan)가 됨

### 7.6 전체 Service 목록

| Service | 주요 기능 |
|---------|----------|
| `UserService` | 회원가입, 조회, 수정, 삭제 |
| `ChannelService` | 채널 생성(PUBLIC/PRIVATE), 조회, 수정, 삭제 |
| `MessageService` | 메시지 작성, 조회, 수정, 삭제 |
| `AuthService` | 로그인 |
| `BinaryContentService` | 파일 저장, 조회, 삭제 |
| `UserStatusService` | 온라인 상태 관리 |
| `ReadStatusService` | 읽음 상태 관리 |

---

## 8. 전체 데이터 흐름 예시

### 8.1 회원가입 시나리오

```
1. 클라이언트가 POST 요청
   {
     "username": "john",
     "email": "john@example.com",
     "password": "1234"
   }

2. Controller가 요청을 받아 UserCreateRequest로 변환

3. BasicUserService.create() 호출
   ├─ userRepository.existsByUsername("john") → false
   ├─ userRepository.existsByEmail("john@example.com") → false
   ├─ new User("john", "john@example.com", "1234", null)
   ├─ userRepository.save(user) → UUID: abc-123
   └─ userStatusRepository.save(new UserStatus(abc-123, now))

4. UserResponse 생성 및 반환
   {
     "id": "abc-123",
     "username": "john",
     "email": "john@example.com",
     "profileId": null,
     "isOnline": true,
     "createdAt": "2025-01-29T...",
     "updatedAt": "2025-01-29T..."
   }
```

### 8.2 비공개 채널 생성 시나리오

```
1. 클라이언트가 POST 요청
   {
     "memberIds": ["user-1", "user-2", "user-3"]
   }

2. BasicChannelService.createPrivate() 호출
   ├─ new Channel(PRIVATE, null, null)  // 이름, 설명 없음
   ├─ channelRepository.save(channel) → UUID: channel-123
   │
   └─ memberIds 각각에 대해 ReadStatus 생성:
      ├─ readStatusRepository.save(new ReadStatus(user-1, channel-123, now))
      ├─ readStatusRepository.save(new ReadStatus(user-2, channel-123, now))
      └─ readStatusRepository.save(new ReadStatus(user-3, channel-123, now))

3. ChannelResponse 생성 및 반환
   {
     "id": "channel-123",
     "type": "PRIVATE",
     "name": null,
     "description": null,
     "participantIds": ["user-1", "user-2", "user-3"],
     ...
   }
```

---

## 9. 직접 구현해보기

### 9.1 새로운 Entity 만들기 체크리스트

```java
// 1. BaseEntity 상속
public class MyEntity extends BaseEntity {

    // 2. 필드 정의 (private)
    private String name;
    private String description;

    // 3. 생성자 (super() 호출 필수!)
    public MyEntity(String name, String description) {
        super();  // ← 이거 빠뜨리면 id, createdAt, updatedAt이 null!
        this.name = name;
        this.description = description;
    }

    // 4. update 메서드 (부분 업데이트)
    public void update(String name, String description) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        updateTimeStamp();  // ← 수정 시간 갱신
    }
}
```

### 9.2 새로운 Repository 만들기 체크리스트

```java
// 1. 인터페이스 정의
public interface MyEntityRepository {
    MyEntity save(MyEntity entity);
    Optional<MyEntity> findById(UUID id);
    List<MyEntity> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
    // 필요한 조회 메서드 추가...
}

// 2. JCF 구현체 (메모리)
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type",
                       havingValue = "jcf", matchIfMissing = true)
public class JCFMyEntityRepository implements MyEntityRepository {

    private final Map<UUID, MyEntity> store = new HashMap<>();

    @Override
    public MyEntity save(MyEntity entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MyEntity> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
    // ... 나머지 구현
}
```

### 9.3 새로운 Service 만들기 체크리스트

```java
// 1. 인터페이스 정의
public interface MyEntityService {
    MyEntityResponse create(MyEntityCreateRequest request);
    MyEntityResponse find(UUID id);
    List<MyEntityResponse> findAll();
    MyEntityResponse update(UUID id, MyEntityUpdateRequest request);
    void delete(UUID id);
}

// 2. 구현체
@Service
@RequiredArgsConstructor
public class BasicMyEntityService implements MyEntityService {

    private final MyEntityRepository myEntityRepository;

    @Override
    public MyEntityResponse create(MyEntityCreateRequest request) {
        // 1. 유효성 검증
        // 2. Entity 생성
        // 3. 저장
        // 4. Response 변환 후 반환
    }
    // ... 나머지 구현
}
```

---

## 10. 자주 하는 실수와 해결법

### 10.1 super() 호출 누락

```java
// ❌ 잘못된 코드
public User(String username, String email, String password) {
    // super() 없음!
    this.username = username;
    this.email = email;
    this.password = password;
}
// 결과: id, createdAt, updatedAt이 모두 null

// ✅ 올바른 코드
public User(String username, String email, String password) {
    super();  // BaseEntity 생성자 호출
    this.username = username;
    this.email = email;
    this.password = password;
}
```

### 10.2 Optional 잘못 사용

```java
// ❌ 잘못된 코드
User user = userRepository.findById(id).get();
// 결과: 없으면 NoSuchElementException (메시지 없음)

// ✅ 올바른 코드
User user = userRepository.findById(id)
    .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
// 결과: 명확한 에러 메시지
```

### 10.3 삭제 순서 실수

```java
// ❌ 잘못된 순서
userRepository.deleteById(id);           // User 먼저 삭제
userStatusRepository.deleteByUserId(id); // 이미 User가 없어서 참조 무결성 문제

// ✅ 올바른 순서
userStatusRepository.deleteByUserId(id); // 연관 데이터 먼저
userRepository.deleteById(id);           // 주 엔티티 나중에
```

### 10.4 null 체크 누락

```java
// ❌ 잘못된 코드
binaryContentRepository.deleteById(user.getProfileId());
// 결과: profileId가 null이면 에러

// ✅ 올바른 코드
if (user.getProfileId() != null) {
    binaryContentRepository.deleteById(user.getProfileId());
}
```

### 10.5 updateTimeStamp() 호출 누락

```java
// ❌ 잘못된 코드
public void update(String name) {
    if (name != null) this.name = name;
    // updateTimeStamp() 없음!
}
// 결과: updatedAt이 갱신되지 않음

// ✅ 올바른 코드
public void update(String name) {
    if (name != null) this.name = name;
    updateTimeStamp();  // 수정 시간 갱신
}
```

---

## 마무리

이 가이드를 통해 Discodeit 프로젝트의 구조를 이해하셨길 바랍니다.

**학습 순서 추천**:
1. Entity 코드 읽기 → 데이터 구조 이해
2. DTO 코드 읽기 → 입출력 형식 이해
3. Repository 인터페이스 읽기 → 저장 방식 이해
4. Service 코드 읽기 → 비즈니스 로직 이해
5. 직접 새 기능 추가해보기!

**질문이 있다면 코드의 주석을 참고하세요. 모든 주요 클래스와 메서드에 상세한 한국어 주석이 있습니다.**

---

*작성일: 2025-01-29*
*작성자: 이종호 (with Claude)*
