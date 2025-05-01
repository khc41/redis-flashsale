package study.redis.flashsale;

import study.redis.flashsale.service.FlashSaleService;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FlashSaleTestHelper {

    public static final int NUM_OF_THREADS = 100;
    public static final int TOTAL_REQUEST = 10000;
    public static final long PRODUCT_ID = 176192437032710244L;
    public static final int STOCK_QUANTITY = 100;

    public static void testPurchase(FlashSaleService flashSaleService) {
        flashSaleService.tryPurchase(PRODUCT_ID, String.valueOf(UUID.randomUUID()));

        assertThat(flashSaleService.getStockCount(PRODUCT_ID)).isEqualTo(STOCK_QUANTITY - 1);
        assertThat(flashSaleService.getOrderCount(PRODUCT_ID)).isEqualTo(1);
    }

    public static void testPurchaseMulti(FlashSaleService flashSaleService) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
        CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUEST);

        IntStream.range(0, TOTAL_REQUEST).forEach(i ->
                executor.submit(() -> {
                    try {
                        flashSaleService.tryPurchase(
                                PRODUCT_ID,
                                String.valueOf(UUID.randomUUID()));
                    } finally {
                        countDownLatch.countDown();
                    }
                })
        );

        countDownLatch.await();
        executor.shutdown();

        assertThat(flashSaleService.getStockCount(PRODUCT_ID)).isEqualTo(0);
        assertThat(flashSaleService.getOrderCount(PRODUCT_ID)).isEqualTo(STOCK_QUANTITY);
    }
}
