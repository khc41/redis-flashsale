package study.redis.flashsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import study.redis.flashsale.domain.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying
    @Query("update Stock s set s.quantity = s.quantity - 1 where s.productId = :productId and s.quantity > 0")
    int decreaseStock(Long productId);
}
