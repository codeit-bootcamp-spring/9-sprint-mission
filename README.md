# Discodeit

[![codecov](https://codecov.io/gh/devseongjun/9-sprint-mission/branch/%EC%9C%A4%EC%84%B1%EC%A4%80/graph/badge.svg)](https://codecov.io/gh/devseongjun/9-sprint-mission)

Discord를 모티브로 한 채팅 서비스 백엔드 애플리케이션입니다.

---

## 기술 스택

- **Java 17**
- **Spring Boot 3.5**
- **Spring Data JPA**
- **PostgreSQL** (운영), **H2** (테스트)
- **AWS S3** (파일 저장소)
- **JaCoCo** (테스트 커버리지)

---

## 실행 방법

### 로컬 실행

```bash
./gradlew bootRun
```

### Docker Compose

```bash
cd discodeit
cp .env.example .env
docker compose up --build
```

---

## 테스트 실행

```bash
./gradlew test jacocoTestReport
```

> **참고**: `AWSS3Test`는 실제 AWS S3 환경이 필요한 통합 테스트입니다.  
> 로컬에서 실행하려면 프로젝트 루트에 `.env` 파일을 생성하고 `@Disabled`를 제거하세요.

---

## CI / CD

`윤성준` 브랜치로의 Pull Request가 생성되면 GitHub Actions를 통해 자동으로 테스트가
실행되고, [Codecov](https://codecov.io/gh/devseongjun/9-sprint-mission)에 커버리지 리포트가 업로드됩니다.

워크플로우 파일: [`.github/workflows/test.yml`](../.github/workflows/test.yml)

---

## 스토리지 설정

| 환경 변수                             | 설명                       | 기본값                  |
|-----------------------------------|--------------------------|----------------------|
| `STORAGE_TYPE`                    | 스토리지 타입 (`local` / `s3`) | `local`              |
| `STORAGE_LOCAL_ROOT_PATH`         | 로컬 저장 경로                 | `.discodeit/storage` |
| `AWS_S3_ACCESS_KEY`               | AWS 액세스 키                | -                    |
| `AWS_S3_SECRET_KEY`               | AWS 시크릿 키                | -                    |
| `AWS_S3_REGION`                   | AWS 리전                   | -                    |
| `AWS_S3_BUCKET`                   | S3 버킷 이름                 | -                    |
| `AWS_S3_PRESIGNED_URL_EXPIRATION` | Presigned URL 만료 시간(초)   | `600`                |
