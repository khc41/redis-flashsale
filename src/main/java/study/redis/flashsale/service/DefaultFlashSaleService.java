package study.redis.flashsale.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.redis.flashsale.domain.Order;
import study.redis.flashsale.repository.FlashSaleJpaRepository;

@Service("defaultFlashSaleService")
@RequiredArgsConstructor
public class DefaultFlashSaleService implements FlashSaleService {

    private final FlashSaleJpaRepository flashSaleJpaRepository;

    @Override
    public void tryPurchase(String userId, int maxCount) {
        if (maxCount <= getCount()) {
            return;
        }
        flashSaleJpaRepository.save(new Order(userId));
    }

    @Override
    public int getCount() {
        return (int) flashSaleJpaRepository.count();
    }
}
