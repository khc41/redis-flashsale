package study.redis.flashsale.service;

public interface FlashSaleService {
    void tryPurchase(Long productId, String userId);

    long getStockCount(Long productId);

    Long getOrderCount(Long productId);

    void init(Long productId);
}
