package study.redis.flashsale.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.repository.OrderRepository;
import study.redis.flashsale.repository.StockRepository;
import study.redis.flashsale.util.Snowflake;

import java.util.concurrent.TimeUnit;

@Slf4j
@Transactional
@Service("redissonFlashSaleService")
@RequiredArgsConstructor
public class RedissonFlashSaleService implements FlashSaleService {
    private static final String PRODUCT_KEY = "flash_sale_product_redisson";
    private final OrderRepository orderRepository;
    private final RedissonClient redissonClient;
    private final Snowflake snowflake;
    private final StockRepository stockRepository;
    private final EntityManager entityManager;

    @Override
    public void tryPurchase(Long productId, String userId) {
        RLock lock = redissonClient.getLock(PRODUCT_KEY);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                log.info("lock 획득 실패");
                return;
            }
            int updated = stockRepository.decreaseStock(productId);
            if (updated == 0) {
                return;
            }
            orderRepository.save(Order.create(
                    snowflake.nextId(),
                    userId,
                    productId
            ));
            entityManager.flush();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public long getStockCount(Long productId) {
        return stockRepository.findById(productId).orElseThrow().getQuantity();
    }

    @Override
    public Long getOrderCount(Long productId) {
        return orderRepository.findCountByProductId(productId);
    }

    @Override
    public void init(Long productId) {
        orderRepository.deleteByProductId(productId);
        stockRepository.findById(productId).ifPresent(stock -> {
            stock.setQuantity(100L);
            stockRepository.save(stock);
        });
    }


}
