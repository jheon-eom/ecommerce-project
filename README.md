# ecommerce-project

# 분산 E-commerce 플랫폼 개발 커리큘럼
**대상:** Java/Kotlin, Spring Boot 5년차 백엔드 개발자  
**목표:** 분산 시스템 설계, MSA, DevOps 실무 역량 강화  
**총 기간:** 약 12-16주 (주말 포함 파트타임 기준)

---

## Phase 1: 프로젝트 설계 및 환경 구성 (1-2주)

### Week 1: 아키텍처 설계
- **도메인 모델링**
  - 주요 도메인: User, Product, Order, Payment, Inventory, Notification
  - 도메인 간 경계 및 의존성 정의
  - Event Storming으로 비즈니스 플로우 시각화

- **시스템 아키텍처 설계**
  - MSA 서비스 분리 전략 수립
  - API Gateway 패턴 설계
  - 동기/비동기 통신 전략 (REST API, Message Queue)
  - 데이터베이스 전략 (Database per Service)

- **기술 스택 선정**
  - 언어: Kotlin + Spring Boot 3.x
  - API Gateway: Spring Cloud Gateway (로컬) → Kubernetes Ingress (배포)
  - Service Discovery: Kubernetes Service (Native DNS)
  - Config Management: Kubernetes ConfigMap/Secret
  - Message Broker: Kafka or RabbitMQ
  - Cache: Redis
  - Database: PostgreSQL (주문/결제), MongoDB (상품 카탈로그)
  - Search: Elasticsearch
  - Optional: Service Mesh (Istio) - Phase 6에서 도입 고려

### Week 2: 개발 환경 구성
- **로컬 개발 환경**
  - Docker Compose로 전체 인프라 구성
  - 각 서비스별 독립 실행 가능하도록 설정
  - Hot Reload 설정

- **버전 관리 전략**
  - Monorepo vs Multi-repo 결정
  - Git Flow 브랜치 전략
  - Conventional Commits 규칙

- **코드 품질 도구 설정**
  - Ktlint, Detekt (코드 스타일)
  - JaCoCo (테스트 커버리지)
  - SonarQube (정적 분석)

---

## Phase 2: 핵심 마이크로서비스 개발 (4-5주)

### Week 3-4: User & Product Service
**User Service (인증/인가)**
- Spring Security + JWT 기반 인증
- OAuth2 소셜 로그인 (Google, Kakao)
- Redis를 활용한 Refresh Token 관리
- 역할 기반 권한 제어 (RBAC)

**Product Service (상품 관리)**
- 상품 CRUD with MongoDB
- Elasticsearch 연동 (전문 검색)
- 검색 자동완성, 필터링, 정렬
- Redis 캐싱 전략 (Cache-Aside 패턴)
- 캐시 무효화 전략

**학습 포인트:**
- Spring WebFlux vs MVC 성능 비교
- 검색 엔진 인덱싱 전략
- 캐시 Stampede 문제 해결

### Week 5-6: Order & Payment Service
**Order Service (주문 관리)**
- 주문 생성/조회/취소
- 주문 상태 머신 구현 (State Pattern)
- Saga 패턴으로 분산 트랜잭션 관리
- Outbox Pattern으로 이벤트 발행 보장

**Payment Service (결제)**
- 외부 PG 연동 시뮬레이션
- 결제 멱등성 보장 (Idempotency Key)
- 결제 실패 시 보상 트랜잭션
- Circuit Breaker 패턴 (Resilience4j)

**학습 포인트:**
- 2PC vs Saga 패턴
- 이벤트 소싱 기초
- 분산 트랜잭션 보상 처리

### Week 7: Inventory & Notification Service
**Inventory Service (재고 관리)**
- 재고 차감 동시성 제어
- Redis 분산 락 vs Database 락 비교
- 재고 부족 시 대기열 처리
- 주기적 재고 동기화 배치

**Notification Service (알림)**
- Kafka Consumer로 이벤트 구독
- 이메일, SMS, Push 알림 추상화
- 알림 템플릿 관리
- 알림 발송 실패 재시도 로직

**학습 포인트:**
- 동시성 제어 전략 비교
- 이벤트 기반 아키텍처 (EDA)
- Dead Letter Queue 처리

---

## Phase 3: 인프라 및 Gateway 구성 (2주)

### Week 8: API Gateway & 서비스 통신
**로컬 환경 - Spring Cloud Gateway**
- Spring Cloud Gateway 구성
- 라우팅, 필터링, Rate Limiting
- JWT 검증 Global Filter
- CORS 설정
- API 문서 통합 (Swagger Aggregation)

**서비스 간 통신 패턴**
- RestTemplate vs WebClient 비교
- Feign Client 사용 (선택적)
- 서비스 호출 시 Retry, Timeout 설정
- Circuit Breaker 패턴 적용

**Kubernetes 환경 준비**
- 서비스 간 DNS 기반 통신 설계 (예: http://payment-service:8080)
- Ingress Controller 전략 수립 (Nginx Ingress)
- 환경 변수로 엔드포인트 주입 패턴

**학습 포인트:**
- Kubernetes Service Discovery 이해
- 로컬(Docker Compose) vs Kubernetes 환경 차이
- Circuit Breaker Dashboard

### Week 9: 통합 및 테스트
**통합 테스트**
- TestContainers로 통합 테스트 환경
- 각 서비스별 Contract Test
- E2E 시나리오 테스트

**성능 테스트**
- Gatling으로 부하 테스트 시나리오 작성
- 동시 접속 1000명 주문 시나리오
- 병목 지점 식별 및 개선
- JMeter로 API 성능 측정

**학습 포인트:**
- 테스트 피라미드 전략
- 성능 튜닝 기법

---

## Phase 4: 관측성 (Observability) 구축 (1-2주)

### Week 10: Logging & Monitoring
**중앙화된 로깅**
- ELK Stack 구성 (Elasticsearch, Logstash, Kibana)
- 각 서비스에 Logback 설정
- Correlation ID로 분산 로그 추적
- 로그 레벨별 필터링 및 알림

**메트릭 수집**
- Micrometer + Prometheus
- 커스텀 메트릭 정의 (주문 수, 결제 성공률 등)
- Grafana 대시보드 구성
- Golden Signals 모니터링
  - Latency: 응답 시간
  - Traffic: RPS
  - Errors: 에러율
  - Saturation: CPU, Memory 사용률

**학습 포인트:**
- 효과적인 로그 설계
- SLO/SLI 정의

### Week 11: Distributed Tracing & Alerting
**분산 추적**
- Spring Cloud Sleuth + Zipkin (또는 Micrometer Tracing + Tempo)
- 요청 흐름 시각화
- 병목 구간 분석

**알림 설정**
- Prometheus Alertmanager
- Slack, Email 알림 연동
- 알림 룰 정의 (CPU 90% 초과, 에러율 5% 초과 등)
- On-call 시뮬레이션

**학습 포인트:**
- Observability의 3 Pillars
- 효과적인 알림 전략 (Alert Fatigue 방지)

---

## Phase 5: DevOps & 자동화 (3-4주)

### Week 12: 컨테이너화 & CI/CD
**Docker 컨테이너화**
- 각 서비스 Dockerfile 작성 (Multi-stage build)
- Docker Compose로 전체 스택 실행
- 이미지 최적화 (레이어 캐싱, 경량 베이스 이미지)

**CI/CD 파이프라인 (선택지)**

**Option 1: GitHub Actions (추천)**
- Public 저장소: 완전 무료
- Private 저장소: 월 2,000분 무료
- 워크플로우 구성:
  1. Lint & Code Quality Check
  2. Unit & Integration Test
  3. Build & Docker Image Push
  4. Security Scan (Trivy)
  5. Deploy to Staging
- 자동화된 테스트 및 배포
- 실패 시 Slack 알림

**Option 2: GitLab CI/CD (완전 무료)**
- Private 저장소도 월 400분 무료
- GitLab Runner를 자체 서버에 설치하면 무제한
- `.gitlab-ci.yml`로 파이프라인 정의

**Option 3: Jenkins (완전 무료, 오픈소스)**
- 자체 서버에 설치 (Docker로 간편 설치)
- 무제한 빌드, 완전한 커스터마이징
- Jenkinsfile로 Pipeline as Code
- 관리 부담은 있지만 학습 가치 높음

**Option 4: GitLab Runner on Local (추천 - 무제한)**
- GitLab에 코드만 올리고
- 본인 PC나 클라우드 인스턴스에 Runner 설치
- 완전 무료 + 무제한 빌드 시간

**학습 포인트:**
- CI/CD 베스트 프랙티스
- Container Security

### Week 13-14: Kubernetes 배포
**Kubernetes 클러스터 구성**
- Local: Minikube or Kind
- Cloud: EKS (AWS) or GKE (Google Cloud)

**리소스 정의**
- Deployment, Service, ConfigMap, Secret
- Ingress Controller (Nginx)
- HPA (Horizontal Pod Autoscaler)
- PersistentVolume for Stateful Services

**Kubernetes Native Service Discovery**
- Service DNS 설정 및 테스트
- ClusterIP vs NodePort vs LoadBalancer
- Headless Service (StatefulSet용)
- 환경 변수로 서비스 엔드포인트 주입
  ```yaml
  env:
  - name: PAYMENT_SERVICE_URL
    value: "http://payment-service:8080"
  ```

**Helm Chart 패키징**
- 각 서비스별 Helm Chart
- Values 파일로 환경별 설정 관리
- Helm Hooks로 마이그레이션 자동화

**학습 포인트:**
- Kubernetes 핵심 개념
- 선언적 인프라 관리
- Kubernetes Service Discovery vs Eureka 차이점 이해

### Week 15: Infrastructure as Code
**Terraform으로 AWS 인프라 구성**
- VPC, Subnet, Security Group
- EKS Cluster
- RDS (PostgreSQL), ElastiCache (Redis)
- S3, CloudFront
- Route53 DNS 설정

**환경별 관리**
- Terraform Workspace (dev, staging, prod)
- 상태 파일 원격 저장 (S3 + DynamoDB)
- 민감 정보 관리 (AWS Secrets Manager)

**학습 포인트:**
- IaC 베스트 프랙티스
- 클라우드 비용 최적화

### Week 16: GitOps & 고급 배포 전략
**GitOps 구현**
- ArgoCD 설정
- Git 기반 배포 자동화
- 배포 히스토리 추적 및 롤백

**배포 전략**
- Blue-Green Deployment
- Canary Deployment (점진적 배포)
- 배포 자동 롤백

**학습 포인트:**
- GitOps 원칙
- 무중단 배포 전략

---

## Phase 6: 안정성 & 최적화 (2주)

### Week 17: 장애 대응 및 카오스 엔지니어링
**Resilience 패턴 적용**
- Circuit Breaker 테스트
- Retry & Fallback 전략
- Bulkhead Pattern (리소스 격리)

**카오스 엔지니어링**
- Chaos Monkey로 무작위 Pod 종료
- 네트워크 지연 시뮬레이션
- DB 장애 상황 대응
- 장애 복구 시간 측정

**Runbook 작성**
- 장애 시나리오별 대응 매뉴얼
- 롤백 절차
- 주요 지표 및 로그 확인 방법

**학습 포인트:**
- 장애 전파 방지
- MTTR(Mean Time To Recovery) 최소화

### Week 18: 성능 최적화 & 보안
**성능 최적화**
- 데이터베이스 쿼리 최적화
- N+1 문제 해결
- Connection Pool 튜닝
- Redis 캐시 전략 고도화
- CDN 활용 (정적 리소스)

**보안 강화**
- API Rate Limiting (Bucket4j)
- SQL Injection, XSS 방어
- OWASP Top 10 체크리스트
- Secrets 관리 (Kubernetes Secrets + External Secrets Operator)
- 네트워크 정책 (NetworkPolicy)

**Optional: Service Mesh 도입**
- Istio 또는 Linkerd 설치
- Traffic Management (A/B Testing, Canary)
- mTLS로 서비스 간 통신 암호화
- Observability 강화 (자동 메트릭, 트레이싱)

**학습 포인트:**
- APM 도구 활용
- 보안 베스트 프랙티스
- Service Mesh vs Kubernetes Native 비교

---

## 최종 결과물

### 1. 기술 문서
- 시스템 아키텍처 다이어그램
- API 명세서 (Swagger/OpenAPI)
- 배포 가이드
- 장애 대응 Runbook
- 성능 테스트 보고서

### 2. 코드 저장소
- 모든 서비스 소스 코드
- Infrastructure as Code (Terraform)
- Helm Charts
- CI/CD 파이프라인 설정
- 테스트 코드 (80% 이상 커버리지)

### 3. 운영 대시보드
- Grafana 모니터링 대시보드
- Kibana 로그 분석 대시보드
- ArgoCD 배포 현황

### 4. 포트폴리오
- GitHub README에 프로젝트 소개
- 기술 블로그 포스팅 (각 단계별 학습 내용)
- 발표 자료 (아키텍처 설명)

---

## 학습 팁

### 단계별 진행 방식
1. **각 Phase를 순차적으로 진행**하되, 너무 완벽하게 하려다 진도가 안 나가지 않도록 주의
2. **MVP(Minimum Viable Product) 먼저 구현** 후 점진적 개선
3. **매주 회고** 작성 (배운 점, 어려웠던 점, 다음 주 목표)
4. **기술 블로그 작성**으로 학습 내용 정리

### 추가 학습 자료
- 책: "마이크로서비스 패턴" (크리스 리처드슨), "쿠버네티스 인 액션"
- 강의: Udemy Kubernetes, AWS 공식 문서
- 커뮤니티: 스프링 캠프, DevOps Korea, AWS User Group

### 실무 적용 포인트
- 회사에서 겪은 문제를 프로젝트에 반영
- 코드 리뷰 요청 (커뮤니티, 멘토)
- 오픈소스 기여 (Spring Cloud, Kubernetes 등)

---

## Kubernetes 환경 전환 체크리스트

### 로컬 개발 (Docker Compose)
- Spring Cloud Gateway 사용
- 서비스 간 직접 호출 (localhost:port)
- 환경 변수로 엔드포인트 설정

### 프로덕션 (Kubernetes)
- Kubernetes Ingress로 전환
- Service DNS 기반 통신 (service-name:port)
- ConfigMap/Secret으로 설정 관리
- Eureka 제거 (불필요)

---

## 확장 아이디어

프로젝트 완성 후 추가로 도전해볼 만한 것들:
- **Service Mesh 도입** (Istio, Linkerd) - Phase 6에서 시작
- **서버리스 통합** (AWS Lambda로 알림 서비스 전환)
- **Multi-Region 배포** (글로벌 서비스 시뮬레이션)
- **Event Sourcing & CQRS** 패턴 적용
- **gRPC로 내부 통신** 전환
- **GraphQL Gateway** 구현
- **AI/ML 통합** (상품 추천, 수요 예측)

이 커리큘럼을 완수하면 시니어 백엔드 개발자로서 분산 시스템 설계, MSA, Kubernetes 기반 DevOps 전 영역의 실무 역량을 갖추게 될 것입니다!
