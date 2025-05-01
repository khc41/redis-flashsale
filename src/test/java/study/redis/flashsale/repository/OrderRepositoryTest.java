package study.redis.flashsale.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.util.Snowflake;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderRepositoryTest {
    public static final long PRODUCT_ID = 176192437032710244L;

    @Autowired
    OrderRepository orderRepository;

    Snowflake snowflake = new Snowflake();


    @Test
    @DisplayName("count 기능 테스트")
    void countTest() {
        // given
        orderRepository.save(
                Order.create(
                        snowflake.nextId(),
                        UUID.randomUUID().toString(),
                        PRODUCT_ID
                )
        );

        // when
        Long countByProductId = orderRepository.findCountByProductId(PRODUCT_ID);

        // then
        assertThat(countByProductId).isEqualTo(1L);
    }
}