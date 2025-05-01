package study.redis.flashsale.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    private Long productId;
    @Setter
    private Long quantity;

    public static Stock create(Long id, Long quantity) {
        Stock stock = new Stock();
        stock.productId = id;
        stock.quantity = quantity;
        return stock;
    }

    public boolean decrease() {
        if (quantity <= 0) {
            return false;
        }
        this.quantity--;
        return true;
    }
}
