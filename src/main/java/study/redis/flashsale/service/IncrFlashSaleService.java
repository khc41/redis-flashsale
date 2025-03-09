package study.redis.flashsale.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.repository.FlashSaleJpaRepository;

@Service("incrFlashSaleService")
@RequiredArgsConstructor
public class IncrFlashSaleService implements FlashSaleService {

    private final FlashSaleJpaRepository flashSaleJpaRepository;

    private final StringRedisTemplate redisTemplate;
    private static final String PRODUCT_KEY = "flash_sale_product";

    @Override
    public void tryPurchase(String userId, int maxCount) {
        Long count = redisTemplate.opsForValue().increment(PRODUCT_KEY);

        if (count > maxCount) {
            return;
        }

        flashSaleJpaRepository.save(new Order(userId));
    }

    @Override
    public int getCount() {
        return (int) flashSaleJpaRepository.count();
    }

    @Override
    public void clear() {
        redisTemplate.delete(PRODUCT_KEY);
    }
}
