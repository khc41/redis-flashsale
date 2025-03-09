package study.redis.flashsale.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DefaultFlashSaleServiceTest {

    public static final int MAX_COUNT = 100;
    public static final int NUM_OF_THREADS = 10;
    public static final int START_INDEX = 0;
    public static final int TOTAL_REQUEST = 1000;

    @Autowired
    @Qualifier("defaultFlashSaleService")
    private FlashSaleService defaultFlashSaleService;

    @Test
    @DisplayName("기본 동작 테스트")
    void testPurchase() {
        IntStream.range(START_INDEX, TOTAL_REQUEST).forEach(i ->
                defaultFlashSaleService.tryPurchase(
                        String.valueOf(UUID.randomUUID()), 100)
        );

        assertThat(defaultFlashSaleService.getCount()).isEqualTo(MAX_COUNT);
    }

    @Test
    @DisplayName("동시성 테스트 (동시성 문제 발생)")
    void testPurchaseMulti() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
        CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUEST);

        IntStream.range(START_INDEX, TOTAL_REQUEST).forEach(i ->
                executor.submit(() -> {
                    try {
                        defaultFlashSaleService.tryPurchase(String.valueOf(UUID.randomUUID()), MAX_COUNT);
                    } finally {
                        countDownLatch.countDown();
                    }
                })
        );

        countDownLatch.await();

        assertThat(defaultFlashSaleService.getCount()).isEqualTo(MAX_COUNT);
    }

}