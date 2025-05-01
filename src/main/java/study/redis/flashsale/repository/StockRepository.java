package study.redis.flashsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.redis.flashsale.domain.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
