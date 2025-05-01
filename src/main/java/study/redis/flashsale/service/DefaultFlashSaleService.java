package study.redis.flashsale.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.domain.Stock;
import study.redis.flashsale.repository.OrderRepository;
import study.redis.flashsale.repository.StockRepository;
import study.redis.flashsale.util.Snowflake;

@Service("defaultFlashSaleService")
@Transactional
@RequiredArgsConstructor
public class DefaultFlashSaleService implements FlashSaleService {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final Snowflake snowflake;

    @Override
    public void tryPurchase(Long productId, String userId) {
        Stock stock = stockRepository.findById(productId)
                .orElseThrow();
        if (!stock.decrease()) {
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
        return stockRepository.findById(productId).orElseThrow().getQuantity();
    }

    @Override
    public Long getOrderCount(Long productId) {
        return orderRepository.findCountByProductId(productId);
    }

    @Override
    public void init(Long productId) {
        orderRepository.deleteByProductId(productId);
        Stock stock = stockRepository.findById(productId).orElseThrow();
        stock.setQuantity(100L);
    }

}
