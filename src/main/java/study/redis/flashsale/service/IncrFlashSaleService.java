package study.redis.flashsale.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.repository.OrderRepository;
import study.redis.flashsale.util.Snowflake;

@Transactional
@Service(value = "incrFlashSaleService")
@RequiredArgsConstructor
@Slf4j
public class IncrFlashSaleService implements FlashSaleService {

    public static final int INITAIL_STOCK_QUANTITY = 100;
    private final OrderRepository orderRepository;

    private final Snowflake snowflake;

    private final StringRedisTemplate redisTemplate;
    private static final String PRODUCT_KEY = "flash_sale_product";

    @Override
    public void tryPurchase(Long productId, String userId) {
        Long count = redisTemplate.opsForValue().decrement(getRedisProductKey(productId));
        if (count == null || count < 0) {
            redisTemplate.opsForValue().increment(getRedisProductKey(productId));
            return;
        }
        orderRepository.save(
                Order.create(
                        snowflake.nextId(),
                        userId,
                        productId
                )
        );
    }

    @Override
    public long getStockCount(Long productId) {
        return Long.parseLong(redisTemplate.opsForValue().get(getRedisProductKey(productId)));
    }

    @Override
    public Long getOrderCount(Long productId) {
        return orderRepository.findCountByProductId(productId);
    }

    @Override
    public void init(Long productId) {
        redisTemplate.opsForValue().set(getRedisProductKey(productId), String.valueOf(INITAIL_STOCK_QUANTITY));
        orderRepository.deleteByProductId(productId);
    }

    private static String getRedisProductKey(Long productId) {
        return PRODUCT_KEY + ":" + productId;
    }

}
