package tw.waterballsa.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductPercentageDiscount extends Discount {
    private String productId;
    private double discountRate;

    public ProductPercentageDiscount(String productId, double discountRate) {
        this.productId = productId;
        this.discountRate = discountRate;
    }

    @Override
    public BigDecimal calculateDiscount(Order order) {
        return order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .map(item -> {
                    BigDecimal itemTotal = item.getSubtotal();
                    BigDecimal discountedTotal = itemTotal.multiply(BigDecimal.valueOf(discountRate));
                    return itemTotal.subtract(discountedTotal);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);
    }
}
