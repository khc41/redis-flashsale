package study.redis.flashsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.redis.flashsale.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
