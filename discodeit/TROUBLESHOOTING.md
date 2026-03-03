# 🔧 문제 해결 가이드

디스코드잇 API 테스트 중 발생할 수 있는 문제와 해결 방법을 정리했습니다.

---

## 🔴 로그인 실패 (404 Not Found)

### 증상
```
Login successful | AssertionError: expected response to have status code 200 but got 404
```

또는 응답:
```json
{
  "message": "Invalid username or password",
  "timestamp": "2026-02-09T..."
}
```

### 원인
**DB에 사용자가 존재하지 않습니다!**

디스코드잇은 **JCF (Java Collection Framework) 메모리 기반 Repository**를 사용합니다:
- ⚠️ 애플리케이션 재시작 시 **모든 데이터 삭제**
- ⚠️ 메모리에만 저장되므로 영구 저장되지 않음

### 해결 방법

#### ✅ 올바른 순서:

```
1. 애플리케이션 실행
   ↓
2. 사용자 등록  ← 먼저 이것!
   ↓
3. 로그인  ← 그 다음 이것!
```

#### Postman에서:

**1단계: 사용자 등록**
```
Collection > 1. 사용자 관리 > 사용자 등록
```
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```
**Send** → 201 Created 확인 ✅

**2단계: 로그인**
```
Collection > 2. 권한 관리 > 로그인
```
```json
{
  "username": "testuser",
  "password": "password123"
}
```
**Send** → 200 OK 확인 ✅

---

## 🔴 채널/메시지 조회 실패 (404 Not Found)

### 증상
```json
{
  "message": "Channel not found: {uuid}",
  "timestamp": "..."
}
```

### 원인
- Environment 변수 (`channelId`, `messageId` 등)가 비어있거나 잘못됨
- 애플리케이션 재시작으로 데이터 손실

### 해결 방법

**1. Environment 변수 확인**
- 우측 상단 Environment 아이콘 클릭
- "Discodeit Local" 선택
- `userId`, `channelId`, `messageId` 값 확인

**2. 처음부터 다시 실행**
```
사용자 등록 → 채널 생성 → 메시지 전송
```

---

## 🔴 Required request body is missing

### 증상
```json
{
  "message": "서버 내부 오류가 발생했습니다: Required request body is missing: ...",
  "timestamp": "..."
}
```

### 원인
Body가 제대로 설정되지 않음

### 해결 방법

**Postman에서 확인:**

1. **Body 탭 클릭**
2. **raw** 선택 ✅
3. 우측 드롭다운에서 **JSON** 선택 ✅ (Text가 아님!)
4. JSON 데이터 입력

```json
{
  "username": "testuser",
  ...
}
```

---

## 🔴 애플리케이션이 실행되지 않음

### 증상
```
Failed to connect to localhost port 8080
```

### 원인
Spring Boot 애플리케이션이 실행되지 않음

### 해결 방법

**터미널에서:**
```bash
cd /Users/ijongho/IdeaProjects/9-sprint-mission/discodeit
./gradlew bootRun
```

**또는 IDE에서:**
- Spring Boot Application 실행 버튼 클릭

**실행 확인:**
```bash
lsof -i :8080
```

정상 출력:
```
java    12345 user   50u  IPv6 ...  TCP *:http-alt (LISTEN)
```

---

## 🔴 Environment 변수가 자동 저장되지 않음

### 증상
userId, channelId 등이 Environment에 저장되지 않음

### 원인
Tests 스크립트가 실행되지 않거나 오류 발생

### 해결 방법

**1. Console 확인**
- Postman 하단의 **Console** 탭 확인
- 에러 메시지나 로그 확인

**2. Tests 탭 확인**
- 요청의 **Tests** 탭에 스크립트가 있는지 확인
- 예시:
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("userId", jsonData.id);
}
```

**3. 수동 저장**
- 응답에서 `id` 복사
- Environment 편집 → `userId`에 붙여넣기

---

## 🔴 JSON parse error

### 증상
```json
{
  "message": "서버 내부 오류가 발생했습니다: JSON parse error: ...",
  "timestamp": "..."
}
```

### 원인
JSON 형식이 잘못됨

### 해결 방법

**올바른 JSON 형식:**
```json
{
  "username": "testuser",
  "email": "test@example.com"
}
```

**잘못된 예:**
```json
{
  username: "testuser",  // ❌ 키에 따옴표 없음
  "email": 'test@example.com'  // ❌ 작은따옴표
}
```

**팁:**
- JSON Validator 사용: https://jsonlint.com/
- Postman이 자동으로 검증해줌 (빨간 밑줄)

---

## 🔴 Private channel cannot be updated

### 증상
```json
{
  "message": "Private channel cannot be updated",
  "timestamp": "..."
}
```

### 원인
비공개 채널은 수정할 수 없도록 설계됨

### 해결 방법

**비공개 채널은 수정 불가**
- 공개 채널만 수정 가능
- 비공개 채널을 수정하려면 삭제 후 재생성

---

## 🔴 이메일/사용자명 중복

### 증상
```json
{
  "message": "이 사용자 이름은 이미 존재해요!: testuser",
  "timestamp": "..."
}
```

또는
```json
{
  "message": "이 이메일은 이미 존재해요!: test@example.com",
  "timestamp": "..."
}
```

### 원인
같은 사용자명 또는 이메일로 중복 등록 시도

### 해결 방법

**다른 사용자명/이메일 사용:**
```json
{
  "username": "testuser2",  // ← 변경
  "email": "test2@example.com",  // ← 변경
  "password": "password123"
}
```

---

## 📊 데이터 초기화

### 애플리케이션 재시작 시

**JCF Repository 특성:**
- ✅ 빠른 개발/테스트
- ❌ 영구 저장 안됨
- ❌ 재시작 시 모든 데이터 삭제

**재시작 후 해야 할 일:**
```
1. 사용자 등록
2. 채널 생성
3. 메시지 전송
```

---

## 💡 유용한 팁

### 1. Collection Runner 사용

전체 API를 순서대로 자동 실행:
1. Collection 우클릭 → **Run collection**
2. 순서 설정
3. **Run** 클릭

### 2. Pre-request Script

자동으로 현재 시간 설정:
```javascript
pm.environment.set("currentTime", new Date().toISOString());
```

### 3. Console 활용

- **View → Show Postman Console** (Cmd/Ctrl + Alt + C)
- 모든 요청/응답 로그 확인
- 디버깅에 매우 유용

### 4. Environment 백업

- Environment Export → JSON 파일 저장
- 필요시 다시 Import

---

## 📞 추가 도움

### 로그 확인

**Spring Boot 로그:**
- 애플리케이션 실행 터미널/콘솔
- 에러 스택 트레이스 확인

**Postman Console:**
- Postman 하단 Console 탭
- 요청/응답 상세 정보

### 도구

**Bash 스크립트:**
```bash
/tmp/test_discodeit_api.sh
```

**Python 스크립트:**
```bash
pip install -r requirements.txt
python test_api_automation.py
```

**API 문서:**
```
API_DOCUMENTATION.md
POSTMAN_GUIDE.md
```

---

## ✅ 체크리스트

문제 발생 시 확인:

- [ ] 애플리케이션이 실행 중인가? (`lsof -i :8080`)
- [ ] 올바른 Environment가 선택되었는가? ("Discodeit Local")
- [ ] 사용자를 먼저 등록했는가?
- [ ] Body 탭에서 raw + JSON이 선택되었는가?
- [ ] JSON 형식이 올바른가?
- [ ] Environment 변수가 설정되었는가?
- [ ] URL이 올바른가? (`/auth/login` 등)
- [ ] Method가 올바른가? (POST, GET 등)

---

**Happy Testing! 🎉**
