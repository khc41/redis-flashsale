# 선착순 이벤트 구현 및 동시성 문제 해결

## 메모
- 테스트 내부 @Transactional은 멀티스레드 환경에서 디비를 rollback 해주지 않는다. (수동 rollback 필요) 