# 🚀 Flash Sale System - 대용량 트래픽 선착순 이벤트 시스템

Redis를 활용한 고성능 플래시 세일 시스템으로, 동시성 문제를 해결하고 다양한 구현 방식을 비교 분석합니다.

## 📋 프로젝트 개요

### 핵심 기능
- **선착순 이벤트** 처리 (재고 100개 한정)
- **대용량 동시 요청** 처리 (10,000 요청 동시 처리)
- **동시성 문제** 해결 및 데이터 정합성 보장
- **다양한 구현 방식** 성능 비교

### 기술 스택
- **Backend**: Spring Boot 3.4.3, Java 23
- **Database**: MySQL, JPA/Hibernate
- **Cache**: Redis, Redisson
- **Message Queue**: AWS SQS (준비)
- **Test**: JUnit 5, 멀티스레드 테스트

## 🏗️ 시스템 아키텍처
~~~
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ Client          │  │ Spring Boot     │  │ Redis           │
│ Requests        │─▶│ Application     │─▶│ (Stock Cache)   │
│ (10,000)        │  │                 │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘
│
▼
┌─────────────────┐
│ MySQL           │
│ (Order Data)    │
└─────────────────┘
~~~

## 🔧 구현 방식별 비교

### 1. DefaultFlashSaleService (기본 구현)
```java
// JPA 엔티티 기반 재고 관리
public boolean decrease() {
    if (quantity <= 0) return false;
    this.quantity--;
    return true;
}
```
- **특징**: JPA 낙관적 락 활용
- **문제점**: 동시성 문제 발생 (Race Condition)
- **성능**: ❌ 동시성 환경에서 데이터 정합성 깨짐

### 2. IncrFlashSaleService (Redis 원자적 연산) ⭐ **권장**
```java
// Redis DECREMENT 원자적 연산
Long count = redisTemplate.opsForValue().decrement(getRedisProductKey(productId));
if (count == null || count < 0) {
    redisTemplate.opsForValue().increment(getRedisProductKey(productId)); // 롤백
    return;
}
```
- **특징**: Redis의 원자적 연산 활용
- **장점**: 
  - ✅ 최고 성능
  - ✅ 순서 보장 (선착순)
  - ✅ 데이터 정합성 보장
- **성능**: 🚀 **최적** - 대용량 트래픽 처리 가능

### 3. RedissonFlashSaleService (분산 락)
```java
// Redisson 분산 락 활용
RLock lock = redissonClient.getLock(PRODUCT_KEY);
boolean isLocked = lock.tryLock(1, 3, TimeUnit.SECONDS);
```
- **특징**: 분산 환경에서 락 기반 동시성 제어
- **장점**: 
  - ✅ 데이터 정합성 보장
  - ✅ 분산 환경 대응
- **단점**: 
  - ⚠️ 순서 보장 안됨 (선착순 부적합)
  - ⚠️ 성능 오버헤드

## 📊 성능 테스트 결과

| 구현 방식 | 동시성 안전성 | 순서 보장 | 성능 | 선착순 적합성 |
|----------|-------------|---------|------|-------------|
| Default  | ❌          | ❌      | 🔶 보통 | ❌ |
| Redis INCR | ✅        | ✅      | 🚀 최고 | ✅ |
| Redisson | ✅          | ❌      | 🔶 보통 | ⚠️ |

### 테스트 조건
- **동시 요청**: 10,000개
- **스레드 풀**: 100개
- **재고**: 100개
- **검증**: 정확히 100개 주문 생성, 재고 0개

## 🚨 해결한 주요 문제들

### 1. 멀티스레드 환경에서 @Transactional 롤백 문제
**문제**: 테스트 내부 `@Transactional`이 멀티스레드 환경에서 DB 롤백을 제대로 처리하지 못함

**해결**: 별도의 초기화 메서드(`init()`) 구현으로 테스트 시작 시 명시적 초기화

### 2. Redis 커넥션 풀 고갈 문제
```bash
HikariPool-1 - Connection is not available, request timed out after 30000ms
```
**문제**: 테스트 클래스의 `@Transactional`이 커넥션을 점유하여 풀 고갈

**해결**: 테스트 클래스에서 `@Transactional` 제거

### 3. Redisson 락 획득 후 데이터 불일치
**문제**: JPA 지연 쓰기로 인해 락 획득 시점과 DB 반영 시점 불일치

**해결**: 직접 쿼리 작성 및 `entityManager.flush()` 강제 실행

### 4. 분산락의 순서 보장 한계
**문제**: Redisson 분산락은 동시성은 제어하지만 요청 순서를 보장하지 않음

**결론**: 선착순 이벤트에는 Redis 원자적 연산이 더 적합

## 🎯 핵심 인사이트

### ✅ 권장사항
1. **선착순 이벤트**: Redis INCR/DECR 원자적 연산 사용
2. **대용량 트래픽**: Redis 캐싱으로 DB 부하 분산
3. **데이터 정합성**: 비즈니스 로직에 맞는 동시성 제어 방식 선택

### ⚠️ 주의사항
1. **분산락 != 순서 보장**: 분산락은 정합성만 보장, 순서는 보장하지 않음
2. **트랜잭션 범위**: 멀티스레드 환경에서 트랜잭션 경계 신중히 설정
3. **커넥션 풀**: 대용량 처리 시 커넥션 풀 설정 최적화 필요

## 🚀 실행 방법

### 사전 요구사항
- Java 23+
- MySQL 8.0+
- Redis 6.0+

### 로컬 실행
```bash
# 1. 의존성 설치
./gradlew build

# 2. MySQL 데이터베이스 생성
mysql -u root -p
CREATE DATABASE flashsale;

# 3. Redis 서버 실행
redis-server

# 4. 애플리케이션 실행
./gradlew bootRun

# 5. 테스트 실행
./gradlew test
```

### 환경 설정
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/flashsale
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
```

## 📝 테스트 시나리오

### 기본 기능 테스트
```java
@Test
void testPurchase() {
    flashSaleService.tryPurchase(PRODUCT_ID, userId);
    
    assertThat(flashSaleService.getStockCount(PRODUCT_ID)).isEqualTo(99);
    assertThat(flashSaleService.getOrderCount(PRODUCT_ID)).isEqualTo(1);
}
```

### 동시성 테스트
```java
@Test
void testPurchaseMulti() throws InterruptedException {
    // 10,000개 요청을 100개 스레드로 동시 처리
    ExecutorService executor = Executors.newFixedThreadPool(100);
    CountDownLatch countDownLatch = new CountDownLatch(10000);
    
    // 결과: 정확히 100개 주문, 재고 0개
    assertThat(flashSaleService.getStockCount(PRODUCT_ID)).isEqualTo(0);
    assertThat(flashSaleService.getOrderCount(PRODUCT_ID)).isEqualTo(100);
}
```

**💡 이 프로젝트는 대용량 트래픽 환경에서의 동시성 제어와 성능 최적화에 대한 실무적 접근을 담고 있습니다.**