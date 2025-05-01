package study.redis.flashsale.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.redis.flashsale.FlashSaleTestHelper;

import static study.redis.flashsale.FlashSaleTestHelper.PRODUCT_ID;

@SpringBootTest
@Transactional
class IncrFlashSaleServiceTest {

    @Autowired
    @Qualifier("incrFlashSaleService")
    private FlashSaleService incrFlashSaleService;

    @BeforeEach
    void init() {
        incrFlashSaleService.init(PRODUCT_ID);
    }

    @Test
    @DisplayName("기본 동작 테스트")
    void testPurchase() {
        FlashSaleTestHelper.testPurchase(incrFlashSaleService);
    }

    @Test
    @DisplayName("동시성 테스트 (동시성 문제 해결)")
    void testPurchaseMulti() throws InterruptedException {
        FlashSaleTestHelper.testPurchaseMulti(incrFlashSaleService);
    }
}