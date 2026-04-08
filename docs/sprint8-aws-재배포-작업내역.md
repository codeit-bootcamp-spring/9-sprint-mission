# 스프린트 8 AWS 재배포 작업 내역 (메모리 최적화)

---

## 배경

이전 배포에서 t3.micro(1GB RAM)의 ECS 환���에서 Spring Boot + JPA 애플리케이션이 OOM(Out of Memory)으로 실패했습니다.
JVM 기본 설정에서 Metaspace가 무제한 확장되고, Tomcat 기본 스레드 200개가 각각 1MB 스택을 소비하면서 메모리가 초과되었습니다.

## 해결 방법

### 1. application-prod.yaml 최적화

| 설정 | 변경 내용 | 효과 |
|------|----------|------|
| Hibernate 쿼리 플랜 캐시 | 2048 → 64 | Metaspace 절약 |
| JMX | 비활성화 | MBean 메모리 절약 |
| 빈 지연 초기화 | 활성화 | 기동 시 메모리 피크 감소 |
| Tomcat 스레드 | max 200 → 20, min-spare 10 → 2 | 스레드 스택 ~180MB 절약 |

### 2. JVM_OPTS 메모리 제한

```
-Xmx256m -Xms128m -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=48m -XX:MaxDirectMemorySize=16m -XX:+UseSerialGC -XX:-TieredCompilation -Xss512k
```

| 옵션 | 값 | 설명 |
|------|-----|------|
| -Xmx | 256m | Heap 상한 |
| -Xms | 128m | 초기 Heap |
| -XX:MaxMetaspaceSize | 128m | 클래스 메타데이터 ���한 |
| -XX:ReservedCodeCacheSize | 48m | JIT 코드 캐시 제한 |
| -XX:MaxDirectMemorySize | 16m | Direct Buffer 제한 |
| -XX:+UseSerialGC | - | GC 스레드 1개 (메모리 절약) |
| -XX:-TieredCompilation | - | JIT 단순화 (Code Cache 절약) |
| -Xss | 512k | 스레드 스택 1MB → 512KB |

### 예상 메모리 사용량

```
Heap              256MB
Metaspace         128MB
Thread Stack       10MB (20스레드 × 512KB)
Code Cache         48MB
기타               30MB
───────────────────────
합계             ~472MB (태스크 제한 900MB 이내)
```

---

## 재생성된 AWS 리소스

| 리소스 | 이름/식별자 | 비고 |
|--------|------------|------|
| S3 버킷 | discodeit-ecs-config-ljh | ECS 환경변수 파일용 (재생성) |
| ECR 퍼블릭 | public.ecr.aws/q1y9z3p9/discodeit | latest, 1.2-M8 태그 (재생성) |
| ECS 클러스터 | discodeit-cluster | 재생성 |
| ECS 서비스 | discodeit-service | 재생성 |
| ECS 태스크 정의 | discodeit-task:4 | 메모리 900MB, 컨테이너 512/900MB |
| EC2 (ECS) | i-0e4489704cb3116a9 (t3.micro) | 컨테이너 인스턴스 |
| RDS | discodeit-db (db.t4g.micro, PostgreSQL 17) | 재생성 |
| 보안그룹 | sg-010f870b656f5c5e2 (ecs-discodeit-sg) | HTTP 80 + SSH 22 |
| 보안그룹 | sg-0dd2845b15bb8e4e0 (rds-ssh-sg) | SSH 터널링용 |
| 키페어 | rds-ssh-key, ecs-cluster-key | 재생성 |

### 기존 유지 리소스

| 리소스 | 이름/식별자 |
|--------|------------|
| S3 버킷 | discodeit-binary-content-storage-ljh-633267 |
| IAM 사용자 | discodeit (S3FullAccess + ECR) |
| IAM 역할 | ecsInstanceRole, ecsTaskExecutionRole |
| VPC | vpc-0b0d796a62c4b7712 (기본 VPC) |

---

## CLI 명령어 (실행 순서)

### 1. discodeit.env 생성 및 S3 업로드

```bash
# S3 버킷 생성
aws s3api create-bucket --bucket discodeit-ecs-config-ljh --region ap-northeast-2 \
  --create-bucket-configuration LocationConstraint=ap-northeast-2

# discodeit.env 작성 (JVM_OPTS 메모리 제한 포함)
cat > /tmp/discodeit.env <<'EOF'
SPRING_PROFILES_ACTIVE=prod
STORAGE_TYPE=s3
AWS_S3_ACCESS_KEY=AKIAZG4NVV43Q7CUMIHB
AWS_S3_SECRET_KEY=<시크릿_키>
AWS_S3_REGION=ap-northeast-2
AWS_S3_BUCKET=discodeit-binary-content-storage-ljh-633267
AWS_S3_PRESIGNED_URL_EXPIRATION=600
SPRING_DATASOURCE_URL=jdbc:postgresql://discodeit-db.cpk6ag6ga2mu.ap-northeast-2.rds.amazonaws.com:5432/discodeit
SPRING_DATASOURCE_USERNAME=discodeit_user
SPRING_DATASOURCE_PASSWORD=discodeit1234
JVM_OPTS=-Xmx256m -Xms128m -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=48m -XX:MaxDirectMemorySize=16m -XX:+UseSerialGC -XX:-TieredCompilation -Xss512k
EOF

aws s3 cp /tmp/discodeit.env s3://discodeit-ecs-config-ljh/discodeit.env
```

### 2. ECR 이미지 빌드 및 push

```bash
aws ecr-public create-repository --repository-name discodeit --region us-east-1

aws ecr-public get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin public.ecr.aws

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t public.ecr.aws/q1y9z3p9/discodeit:latest \
  -t public.ecr.aws/q1y9z3p9/discodeit:1.2-M8 \
  --push .
```

### 3. RDS 생성 및 DB 초기화

```bash
aws rds create-db-instance \
  --db-instance-identifier discodeit-db \
  --db-instance-class db.t4g.micro \
  --engine postgres --engine-version 17.2 \
  --master-username postgres --master-user-password discodeit1234rds \
  --allocated-storage 20 --no-publicly-accessible --no-multi-az \
  --no-auto-minor-version-upgrade --backup-retention-period 0 \
  --no-enable-performance-insights --monitoring-interval 0 \
  --storage-type gp2 --port 5432 --region ap-northeast-2

# SSH 터널링용 EC2 → DB 초기화 → EC2 삭제
# (상세 명령어는 sprint8-aws-작업내역.md 참조)
```

### 4. ECS 클러스터 + 태스크 + 서비스

```bash
# 클러스터 생성
aws ecs create-cluster --cluster-name discodeit-cluster --region ap-northeast-2

# 보안그룹 (HTTP 80)
aws ec2 create-security-group \
  --group-name ecs-discodeit-sg \
  --description "ECS discodeit HTTP access" \
  --vpc-id vpc-0b0d796a62c4b7712 --region ap-northeast-2
# → sg-010f870b656f5c5e2

aws ec2 authorize-security-group-ingress \
  --group-id sg-010f870b656f5c5e2 --protocol tcp --port 80 --cidr 0.0.0.0/0 --region ap-northeast-2

# ECS→RDS 접근 허용
aws ec2 authorize-security-group-ingress \
  --group-id sg-0bea7ae8fbe3e4d1a --protocol tcp --port 5432 \
  --source-group sg-010f870b656f5c5e2 --region ap-northeast-2

# 태스크 실행 역할 정책
aws iam put-role-policy --role-name ecsTaskExecutionRole --policy-name EcsS3EnvAccess \
  --policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":["s3:GetObject","s3:GetBucketLocation"],"Resource":["arn:aws:s3:::discodeit-ecs-config-ljh","arn:aws:s3:::discodeit-ecs-config-ljh/*"]}]}'

aws iam put-role-policy --role-name ecsTaskExecutionRole --policy-name EcsCloudWatchLogs \
  --policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":["logs:CreateLogGroup","logs:CreateLogStream","logs:PutLogEvents"],"Resource":"arn:aws:logs:ap-northeast-2:633267924791:*"}]}'

# ECS EC2 인스턴스 (ECS 최적화 AMI)
aws ec2 run-instances \
  --image-id ami-08a258f1d2deaa642 \
  --instance-type t3.micro \
  --key-name ecs-cluster-key \
  --security-group-ids sg-010f870b656f5c5e2 \
  --subnet-id subnet-00b2848bbf4bde682 \
  --associate-public-ip-address \
  --iam-instance-profile Name=ecsInstanceProfile \
  --user-data "$(printf '#!/bin/bash\necho ECS_CLUSTER=discodeit-cluster >> /etc/ecs/ecs.config' | base64)" \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=ecs-discodeit}]' \
  --region ap-northeast-2

# 태스크 정의 (메모리 최적화)
aws ecs register-task-definition \
  --family discodeit-task \
  --network-mode bridge \
  --requires-compatibilities EC2 \
  --cpu "256" --memory "900" \
  --execution-role-arn arn:aws:iam::633267924791:role/ecsTaskExecutionRole \
  --container-definitions '[{
    "name":"discodeit-app",
    "image":"public.ecr.aws/q1y9z3p9/discodeit:latest",
    "portMappings":[{"containerPort":80,"hostPort":80,"protocol":"tcp"}],
    "cpu":256,"memoryReservation":512,"memory":900,"essential":true,
    "environmentFiles":[{"value":"arn:aws:s3:::discodeit-ecs-config-ljh/discodeit.env","type":"s3"}],
    "logConfiguration":{"logDriver":"awslogs","options":{"awslogs-group":"/ecs/discodeit","awslogs-region":"ap-northeast-2","awslogs-stream-prefix":"ecs","awslogs-create-group":"true"}}
  }]' --region ap-northeast-2

# 서비스 생성
aws ecs create-service \
  --cluster discodeit-cluster \
  --service-name discodeit-service \
  --task-definition discodeit-task:4 \
  --desired-count 1 \
  --launch-type EC2 \
  --health-check-grace-period-seconds 60 \
  --region ap-northeast-2
```

---

## 배포 검증 결과

| 테스트 | 결과 | 비고 |
|--------|------|------|
| ECS 태스크 실행 | 성공 (runningCount: 1) | OOM 없이 기동 |
| Spring Boot 기동 | 154초 | 메모리 제한 환경에서 느리지만 정상 |
| `GET /api/users` | 200 OK | 빈 배열 반환 |
| `GET /actuator/health` | UP (DB 연결 확인) | PostgreSQL 정상 |
| `GET /` (프론트엔드) | 200 OK | 정상 |
| EC2 퍼블릭 IP | 3.39.195.223 | `http://3.39.195.223` 접속 가능 |

---

## 이전 배�� 대비 변경사항

| 항목 | 이전 | 현재 |
|------|------|------|
| 태스크 메모리 | 800MB | 900MB |
| 컨테이너 memoryReservation | 384MB | 512MB |
| JVM Heap | -Xmx384m | -Xmx256m |
| JVM Metaspace | -XX:MaxMetaspaceSize=64m | -XX:MaxMetaspaceSize=128m |
| Code Cache | 제한 없음 | -XX:ReservedCodeCacheSize=48m |
| Direct Buffer | 제한 없음 | -XX:MaxDirectMemorySize=16m |
| 스레드 스택 | 기본 1MB | -Xss512k |
| TieredCompilation | 기본 (활성) | 비활성화 |
| Tomcat 스레드 | 기본 200 | 20 |
| Hibernate 쿼리 캐시 | 기본 2048 | 64 |
| JMX | 기본 (활성) | 비활성화 |
| 빈 초기화 | 즉시 | 지연 초기화 |
| **결과** | **OOM 실패** | **기동 성공 (154초)** |
