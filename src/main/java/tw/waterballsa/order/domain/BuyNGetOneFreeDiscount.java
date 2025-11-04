package tw.waterballsa.order.domain;

import java.math.BigDecimal;

public class BuyNGetOneFreeDiscount extends Discount {
    private String productId;
    private int buyQuantity;

    public BuyNGetOneFreeDiscount(String productId, int buyQuantity) {
        this.productId = productId;
        this.buyQuantity = buyQuantity;
    }

    @Override
    public BigDecimal calculateDiscount(Order order) {
        return order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .map(item -> {
                    int quantity = item.getQuantity();
                    int freeItems = quantity / (buyQuantity + 1);
                    BigDecimal unitPrice = item.getProduct().getPrice();
                    return unitPrice.multiply(BigDecimal.valueOf(freeItems));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
