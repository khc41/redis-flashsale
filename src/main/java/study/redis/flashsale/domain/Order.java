package study.redis.flashsale.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    private Long id;

    @Column(name = "user_id")
    private String userId;

    private Long productId;

    public static Order create(Long id, String userId, Long productId) {
        Order order = new Order();
        order.id = id;
        order.userId = userId;
        order.productId = productId;
        return order;
    }
}
