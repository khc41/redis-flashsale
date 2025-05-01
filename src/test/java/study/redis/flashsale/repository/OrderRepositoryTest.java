package study.redis.flashsale.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.util.Snowflake;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class OrderRepositoryTest {
    public static final long PRODUCT_ID = 176192437032710244L;

    @Autowired
    OrderRepository orderRepository;

    Snowflake snowflake = new Snowflake();

    @BeforeEach
    void init(){
        orderRepository.save(
                Order.create(
                        snowflake.nextId(),
                        UUID.randomUUID().toString(),
                        PRODUCT_ID
                )
        );
    }

    @Test
    @DisplayName("상품아이디로 주문을 삭제한다.")
    void deleteTest(){
        //given

        //when
        orderRepository.deleteByProductId(PRODUCT_ID);

        //then
        assertThat(orderRepository.findCountByProductId(PRODUCT_ID)).isEqualTo(0L);
    }

    @Test
    @DisplayName("주문이 있을 때, 주문 개수를 반환한다.")
    void countTest() {
        //given

        // when
        Long countByProductId = orderRepository.findCountByProductId(PRODUCT_ID);

        // then
        assertThat(countByProductId).isEqualTo(1L);
    }
}