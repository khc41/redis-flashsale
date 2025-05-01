package study.redis.flashsale.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StockTest {

    @Test
    @DisplayName("재고가 0이하일 때 false를 반환한다.")
    void decreaseReturnFalseTest() {
        //given
        Stock stock = Stock.create(1L, 0L);

        //when
        boolean decreased = stock.decrease();

        //then
        assertThat(decreased).isFalse();
    }

    @Test
    @DisplayName("재고가 정상일 때, 재고를 차감한다.")
    void decreaseTest() {
        //given
        Stock stock = Stock.create(1L, 1L);

        //when
        boolean decreased = stock.decrease();

        //then
        assertAll(
                () -> assertThat(decreased).isTrue(),
                () -> assertThat(stock.getQuantity()).isEqualTo(0)
        );
    }

}