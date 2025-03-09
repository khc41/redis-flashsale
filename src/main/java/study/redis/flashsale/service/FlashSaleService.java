package study.redis.flashsale.service;

public interface FlashSaleService {
    void tryPurchase(String userId, int maxCount);

    int getCount();

    default void clear() {

    }
}
