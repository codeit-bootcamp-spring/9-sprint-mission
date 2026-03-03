# 📮 Postman 사용 가이드

디스코드잇 API를 Postman으로 테스트하는 완벽 가이드입니다.

---

## 🚀 빠른 시작

### 1단계: 파일 Import

#### ✅ Collection Import
1. Postman 실행
2. **Import** 버튼 클릭
3. `Discodeit_API_Collection.postman_collection.json` 파일 선택
4. **Import** 클릭

#### ✅ Environment Import
1. Postman에서 **Environments** 탭 선택
2. **Import** 버튼 클릭
3. `Discodeit_Environment.postman_environment.json` 파일 선택
4. **Import** 클릭

### 2단계: Environment 선택

우측 상단의 Environment 드롭다운에서 **"Discodeit Local"** 선택

### 3단계: 애플리케이션 실행

터미널에서:
```bash
cd /Users/ijongho/IdeaProjects/9-sprint-mission/discodeit
./gradlew bootRun
```

또는 IDE에서 Spring Boot 애플리케이션 실행

### 4단계: API 테스트 시작!

Collection에서 원하는 API 선택 → **Send** 버튼 클릭

---

## 📂 Collection 구조

```
디스코드잇 API
├── 1. 사용자 관리
│   ├── 사용자 등록 ← 여기서 시작!
│   ├── 전체 사용자 조회
│   ├── 특정 사용자 조회
│   ├── 사용자 수정
│   ├── 사용자 삭제
│   └── 온라인 상태 업데이트
├── 2. 권한 관리
│   └── 로그인
├── 3. 채널 관리
│   ├── 공개 채널 생성
│   ├── 비공개 채널 생성
│   ├── 사용자별 채널 목록 조회
│   ├── 특정 채널 조회
│   ├── 채널 수정
│   └── 채널 삭제
├── 4. 메시지 관리
│   ├── 메시지 전송
│   ├── 채널별 메시지 조회
│   ├── 특정 메시지 조회
│   ├── 메시지 수정
│   └── 메시지 삭제
├── 5. 메시지 수신 정보
│   ├── 수신 정보 생성
│   ├── 수신 정보 수정
│   └── 사용자별 수신 정보 조회
└── 6. 바이너리 파일
    ├── 파일 1개 조회
    └── 파일 여러 개 조회
```

---

## 🎯 테스트 시나리오

### 시나리오 1: 기본 플로우 (추천)

**목표**: 사용자 생성 → 채널 생성 → 메시지 전송

1. **사용자 등록**
   - `1. 사용자 관리 > 사용자 등록` 실행
   - ✅ userId가 자동으로 Environment에 저장됨

2. **로그인**
   - `2. 권한 관리 > 로그인` 실행
   - ✅ 로그인 성공 확인

3. **공개 채널 생성**
   - `3. 채널 관리 > 공개 채널 생성` 실행
   - ✅ channelId가 자동으로 Environment에 저장됨

4. **메시지 전송**
   - `4. 메시지 관리 > 메시지 전송` 실행
   - ✅ messageId가 자동으로 Environment에 저장됨

5. **메시지 조회**
   - `4. 메시지 관리 > 채널별 메시지 조회` 실행
   - ✅ 방금 전송한 메시지 확인

6. **메시지 수정**
   - `4. 메시지 관리 > 메시지 수정` 실행
   - ✅ 메시지 내용 변경 확인

---

### 시나리오 2: 비공개 채널

**목표**: 비공개 채널 생성 및 참여자 관리

1. **사용자 2명 등록**
   - 첫 번째 사용자 등록 → userId1 복사
   - Body의 username, email 변경 후 다시 실행 → userId2 복사

2. **비공개 채널 생성**
   - `3. 채널 관리 > 비공개 채널 생성`
   - Body 수정:
   ```json
   {
     "memberIds": [
       "{{userId}}",  // 첫 번째 사용자
       "paste-userId2-here"  // 두 번째 사용자
     ]
   }
   ```

3. **채널 목록 조회**
   - Environment의 userId를 첫 번째 사용자로 설정
   - `3. 채널 관리 > 사용자별 채널 목록 조회`
   - ✅ 비공개 채널 포함 확인

   - Environment의 userId를 두 번째 사용자로 설정
   - 다시 조회
   - ✅ 같은 비공개 채널 확인

---

### 시나리오 3: CRUD 전체 테스트

**목표**: 생성-조회-수정-삭제 전체 플로우

#### 사용자 CRUD
```
생성 → 조회 → 수정 → 삭제
```

#### 채널 CRUD
```
생성 → 조회 → 수정 → 삭제
```

#### 메시지 CRUD
```
전송 → 조회 → 수정 → 삭제
```

---

## 🔧 Environment 변수

| 변수명 | 설명 | 자동 저장 |
|--------|------|----------|
| `baseUrl` | API 기본 URL | ❌ 수동 |
| `userId` | 생성된 사용자 ID | ✅ 자동 |
| `channelId` | 생성된 채널 ID | ✅ 자동 |
| `messageId` | 생성된 메시지 ID | ✅ 자동 |
| `readStatusId` | 생성된 수신정보 ID | ✅ 자동 |
| `binaryContentId` | 업로드된 파일 ID | ❌ 수동 |

### 변수 확인 방법

1. 우측 상단 Environment 아이콘 클릭
2. "Discodeit Local" 선택
3. **Current Value** 컬럼에서 저장된 값 확인

### 변수 수동 설정

1. Environment 편집 화면에서 **Current Value** 입력
2. 또는 **Console** (하단)에서 자동 저장 로그 확인:
   ```
   사용자 ID 저장: uuid-value
   채널 ID 저장: uuid-value
   ```

---

## 💡 유용한 팁

### 1️⃣ Tests 스크립트

각 요청에는 자동으로 ID를 저장하는 스크립트가 포함되어 있습니다:

```javascript
// 사용자 등록 후 자동 실행
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("userId", jsonData.id);
    console.log("사용자 ID 저장:", jsonData.id);
}
```

### 2️⃣ 응답 자동 포맷팅

Postman이 자동으로 JSON을 보기 좋게 포맷팅합니다.
- **Pretty** 탭: 읽기 편한 형식
- **Raw** 탭: 원본 JSON
- **Preview** 탭: HTML 렌더링 (해당 시)

### 3️⃣ Collection Runner

전체 API를 자동으로 테스트:

1. Collection 우클릭 → **Run collection**
2. 실행할 요청 선택
3. **Run** 버튼 클릭
4. ✅ 전체 테스트 결과 확인

### 4️⃣ 요청 복사

특정 요청을 curl로 변환:

1. 요청 우클릭 → **Export** → **Request**
2. 또는 **Code** 아이콘 클릭 → curl 선택

---

## 🐛 문제 해결

### 문제 1: "baseUrl is not defined"

**원인**: Environment가 선택되지 않음

**해결**:
1. 우측 상단에서 "Discodeit Local" 선택
2. Environment 변수 확인

### 문제 2: "Failed to connect"

**원인**: Spring Boot 애플리케이션이 실행되지 않음

**해결**:
```bash
# 애플리케이션 실행 확인
lsof -i :8080

# 실행되지 않았다면
./gradlew bootRun
```

### 문제 3: "User not found" / "Channel not found"

**원인**: Environment 변수가 비어있거나 잘못된 UUID

**해결**:
1. 사용자 등록부터 다시 시작
2. Environment의 `userId`, `channelId` 확인

### 문제 4: "Required request body is missing"

**원인**: Body 탭에서 raw + JSON이 선택되지 않음

**해결**:
1. Body 탭 클릭
2. **raw** 선택
3. 우측 드롭다운에서 **JSON** 선택

---

## 📊 추가 도구

### Bash 스크립트
전체 API를 터미널에서 테스트:
```bash
/tmp/test_discodeit_api.sh
```

### Python 스크립트
프로그래밍 방식으로 API 테스트:
```bash
# 의존성 설치
pip install -r requirements.txt

# 실행
python test_api_automation.py
```

### API 문서
상세한 API 문서:
```
API_DOCUMENTATION.md
```

---

## 📞 도움이 필요하신가요?

- **API 문서**: `API_DOCUMENTATION.md` 참조
- **에러 로그**: Postman Console (하단) 확인
- **서버 로그**: Spring Boot 애플리케이션 콘솔 확인

---

**Happy Testing! 🎉**
