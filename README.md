# 선착순 이벤트 구현 및 동시성 문제 해결

## 메모
- 테스트 내부 @Transactional은 멀티스레드 환경에서 디비를 rollback 해주지 않는다. (수동 rollback 필요) 

- Redis desc 로 테스트 중 커넥션 풀 에러 발생
```bash
2025-05-01T15:24:48.330+09:00  WARN 12953 --- [flashsale] [ol-3-thread-100] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 0, SQLState: null
2025-05-01T15:24:48.330+09:00 ERROR 12953 --- [flashsale] [ol-3-thread-100] o.h.engine.jdbc.spi.SqlExceptionHelper   : HikariPool-1 - Connection is not available, request timed out after 30000ms (total=100, active=100, idle=0, waiting=0)
```
-> 확인 결과 Test 클래스 내 @Transactional이 롤백을 하려고 커넥션을 붙잡고 있어서 문제였음.