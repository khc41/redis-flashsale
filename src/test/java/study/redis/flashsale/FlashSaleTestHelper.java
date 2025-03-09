package study.redis.flashsale;

import study.redis.flashsale.service.FlashSaleService;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FlashSaleTestHelper {

    public static final int MAX_COUNT = 100;
    public static final int NUM_OF_THREADS = 100;
    public static final int START_INDEX = 0;
    public static final int TOTAL_REQUEST = 10000;

    public static void testPurchase(FlashSaleService flashSaleService) {
        IntStream.range(START_INDEX, TOTAL_REQUEST).forEach(i ->
                flashSaleService.tryPurchase(
                        String.valueOf(UUID.randomUUID()), MAX_COUNT)
        );

        assertThat(flashSaleService.getCount()).isEqualTo(MAX_COUNT);
    }

    public static void testPurchaseMulti(FlashSaleService flashSaleService) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
        CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUEST);

        IntStream.range(START_INDEX, TOTAL_REQUEST).forEach(i ->
                executor.submit(() -> {
                    try {
                        flashSaleService.tryPurchase(String.valueOf(UUID.randomUUID()), MAX_COUNT);
                    } finally {
                        countDownLatch.countDown();
                    }
                })
        );

        countDownLatch.await();

        assertThat(flashSaleService.getCount()).isEqualTo(MAX_COUNT);
    }
}
