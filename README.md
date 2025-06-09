# 선착순 이벤트 구현 및 동시성 문제 해결

## 발생 문제와 해결 방법

### 1. 테스트 내부 @Transactional은 멀티스레드 환경에서 디비를 rollback 해주지 않음<br>

- 별도의 초기화 메서드를 통해 테스트 시작 시 초기화

### 2. Redis desc 로 테스트 중 커넥션 풀 에러 발생

```bash
2025-05-01T15:24:48.330+09:00  WARN 12953 --- [flashsale] [ol-3-thread-100] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 0, SQLState: null
2025-05-01T15:24:48.330+09:00 ERROR 12953 --- [flashsale] [ol-3-thread-100] o.h.engine.jdbc.spi.SqlExceptionHelper   : HikariPool-1 - Connection is not available, request timed out after 30000ms (total=100, active=100, idle=0, waiting=0)
```

- 확인 결과 Test 클래스 내 @Transactional이 롤백을 하려고 커넥션을 붙잡고 있어서 문제 발생
- Test 클래스 내 @Transactional을 제거해서 해결

### 3. Redisson으로 구현 중, Lock을 획득했지만 재고가 다른 경우 발생

- JPA 특성상 트랜잭션이 커밋될 때 DB를 반영해서 락을 획득하고 로직을 진행해도 다른 스레드에서 진입하는 시점과 맞물려 정합성 문제 발생
- 직접 쿼리를 작성해 해결

### 4. Redisson으로 분산락을 구현했지만, 요구사항에 부합하지 않아서 개발 중단

- 분산락은 순서를 보장하지 않아 선착순 이벤트에 부적합함.