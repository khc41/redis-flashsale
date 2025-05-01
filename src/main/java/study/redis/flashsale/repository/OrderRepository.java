package study.redis.flashsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import study.redis.flashsale.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select count(*) from Order o where o.productId = :productId")
    Long findCountByProductId(Long productId);

    @Modifying
    @Query("delete from Order o where o.productId = :productId")
    void deleteByProductId(Long productId);
}
