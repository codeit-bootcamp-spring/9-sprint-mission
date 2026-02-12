# # Discodeit Sprint 4 - REST API Integration & Data Persistence

## 1. RESTful API Architecture (신규 계층 도입)
기존의 내부 로직을 외부 클라이언트와 통신 가능한 구조로 확장했습니다. Spring MVC를 도입하여 데이터의 입출력을 HTTP 프로토콜로 표준화하고 계층 간 독립성을 확보했습니다.

* **Controller Layer**: `@RestController`를 통해 엔드포인트를 노출하고, 클라이언트의 JSON 요청을 DTO로 매핑하여 서비스 계층으로 전달합니다.
* **Separation of Concerns**: 컨트롤러는 HTTP 요청/응답 처리 및 상태 코드 제어에만 집중하며, 실제 로직은 서비스 계층에 위임하는 4-Layered Architecture를 완성했습니다.

---

## 2. 주요 업데이트 사항 (Sprint 4 핵심)

| 핵심 기능 | 설명 및 구현 내용 |
| :--- | :--- |
| **Base64 Binary Handling** | 이미지 데이터를 Base64 문자열로 수신하여 `byte[]`로 디코딩 후 저장소에 기록하는 파이프라인 구축 |
| **UUID Validation** | ID 참조 시 표준 36자 규격 엄격 검증 로직을 적용하여 데이터 무결성 확보 및 런타임 예외 방지 |
| **Record-based DTO** | Java 17의 `record`를 활용하여 DTO의 불변성(Immutability)을 보장하고 데이터 전송 효율 증대 |
| **Bean Ambiguity Resolve** | 자동(@Repository)과 수동(@Bean) 등록 간의 중복 충돌을 해결하여 의존성 주입(DI)의 안정성 확보 |

---

## 3. 핵심 기술 적용 상세

### 1) Spring MVC & API 규격화
* **Standard Response**: 성공 시 `200 OK`, 데이터 오류 시 `400 Bad Request`, 리소스 부재 시 `404 Not Found` 등 상황별 HTTP 상태 코드를 활용한 명확한 결과 전달 로직 구현
* **JSON Serialization**: Jackson 라이브러리를 통해 객체와 JSON 간의 자동 직렬화/역직렬화(Marshalling) 처리
* **Annotation Routing**: `@PostMapping`, `@GetMapping` 등을 활용하여 직관적이고 유지보수가 용이한 엔드포인트 관리

### 2) 데이터 참조 및 영속성 (Persistence)
* **Entity Relationship**: 유저 생성 시 `BinaryContent`의 ID를 참조하도록 설계하여 도메인 간의 결합도를 낮추면서도 관계성을 유지
* **Interface-driven Development**: `JCF`와 `File` 방식 등 저장소 구현체에 관계없이 동일한 인터페이스 규격(`BinaryContentRepository`)을 준수하여 상위 계층의 코드 변화 최소화

---

## 4. 테스트 시나리오 (End-to-End)
본 프로젝트는 백엔드 엔진의 완벽한 동작을 보장하기 위해 포스트맨(Postman)을 통한 단계별 통합 테스트를 수행했습니다.

1. **Binary Content**: 프로필 이미지(Base64) 전송 및 고유 ID 발급 확인
2. **User Register**: 발급된 ID를 `profileId`로 참조하여 실제 유저 엔티티 등록
3. **Authentication**: 등록된 이메일/비밀번호 기반의 로그인 성공 여부 검증
4. **Messaging**: 유저와 채널 간의 참조 관계를 활용한 실시간 메시지 전송 및 리스트 조회