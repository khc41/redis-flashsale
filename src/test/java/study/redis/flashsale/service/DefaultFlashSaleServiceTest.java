package study.redis.flashsale.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.redis.flashsale.FlashSaleTestHelper;

@Transactional
@SpringBootTest
class DefaultFlashSaleServiceTest {

    @Autowired
    @Qualifier("defaultFlashSaleService")
    private FlashSaleService defaultFlashSaleService;

    @Test
    @DisplayName("기본 동작 테스트")
    void testPurchase() {
        FlashSaleTestHelper.testPurchase(defaultFlashSaleService);
    }

    @Test
    @DisplayName("동시성 테스트 (동시성 문제 발생)")
    void testPurchaseMulti() throws InterruptedException {
        FlashSaleTestHelper.testPurchaseMulti(defaultFlashSaleService);
    }

}