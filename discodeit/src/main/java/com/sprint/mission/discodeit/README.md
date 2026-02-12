# Discodeit Sprint 3 - Spring Boot Migration & Status Management

## 1. 전체 구조 설명 (Spring-Based 4-Layered Architecture)
기존 Java 프로젝트를 **Spring Boot 3.4.0** 환경으로 마이그레이션하며, 객체 관리 및 의존성 주입을 Spring IoC 컨테이너에 위임하여 결합도를 낮추고 확장성을 높였습니다.

* **Entity 계층**: UUID 기반 참조를 유지하며, 시간 필드 타입을 `Instant`로 통일하여 정밀도를 확보
* **Repository 계층**: `discodeit.repository.type` 설정에 따라 JCF(Memory) 또는 File 저장소를 조건부 빈(@Conditional)으로 등록
* **Service 계층**: 비즈니스 로직 수행 및 DTO 변환을 담당하며, `@RequiredArgsConstructor`를 통한 생성자 주입 방식 채택
* **Manager 계층**: 서비스 간 조합을 통해 '메시지 전송 시 유저 상태 갱신'과 같은 고수준 비즈니스 프로세스 처리

## 2. 주요 도메인 및 필드 (Sprint 3 확장)

| 구분 | 이름 | 주요 고도화 내용 |
| :--- | :--- | :--- |
| **UserStatus** | 신규 도메인 | 마지막 접속 시간을 기록하여 **5분 이내 활동 시 ONLINE** 판정 |
| **ReadStatus** | 신규 도메인 | 사용자의 채널별 마지막 메시지 읽음 상태 관리 |
| **BinaryContent** | 신규 도메인 | 프로필 이미지, 메시지 첨부파일 등 바이너리 데이터 관리 (수정 불가 모델) |
| **User** | 기존 확장 | 프로필 이미지(BinaryContent) 연동 및 DTO에서 패스워드 필드 격리 |
| **Channel** | 기존 확장 | PRIVATE/PUBLIC 생성 로직 분리 및 최근 메시지 시간 정보 포함 |

## 3. 핵심 기술 적용 사항

### 1) Spring Framework & IoC/DI
* **IoC 컨테이너 활용**: `ServiceFactory`를 제거하고 Spring Context가 Bean의 생명주기를 관리하도록 리팩토링
* **Dependency Injection**: `@Service`, `@Repository`, `@Component` 어노테이션을 사용하여 빈을 등록하고 의존성을 자동으로 주입
* **Lombok 적용**: `@Getter`, `@RequiredArgsConstructor` 등을 활용하여 보일러플레이트 코드를 제거하고 가독성 향상

### 2) 비즈니스 로직 고도화
* **시간 타입 표준화**: 모든 시간 필드를 `Instant`로 변경하여 가독성 및 시간대(Time Zone) 연산 효율성 확보
* **DTO 기반 데이터 전송**: 엔티티 내부 정보를 보호하기 위해 응답 시 `UserDto.Response`, `ChannelDto.Response` 등을 활용하고 보안상 민감한 패스워드는 완전 제외
* **활동 기반 상태 업데이트**: `updateByUserId` 기능을 통해 유저가 로그인하거나 메시지를 작성할 때마다 접속 시점을 실시간 갱신

## 4. 저장소 관리 (심화 요구사항)
`application.yaml` 설정을 통해 실행 시점에 저장소 타입을 동적으로 결정할 수 있도록 설계했습니다.

* **Dynamic Repository Loading**:
  ```yaml
  discodeit:
    repository:
      type: file           # jcf (메모리) 또는 file (직렬화 파일) 선택
      file-directory: .discodeit # File 저장소 사용 시 저장 경로 설정