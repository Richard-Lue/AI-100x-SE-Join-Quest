package tw.waterballsa.order.domain;

import java.math.BigDecimal;

public class AmountThresholdDiscount extends Discount {
    private BigDecimal threshold;
    private BigDecimal discountAmount;

    public AmountThresholdDiscount(BigDecimal threshold, BigDecimal discountAmount) {
        this.threshold = threshold;
        this.discountAmount = discountAmount;
    }

    @Override
    public BigDecimal calculateDiscount(Order order) {
        BigDecimal originalAmount = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (originalAmount.compareTo(threshold) >= 0) {
            return discountAmount;
        }
        return BigDecimal.ZERO;
    }
}
