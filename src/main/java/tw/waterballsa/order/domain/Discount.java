package tw.waterballsa.order.domain;

import java.math.BigDecimal;

public abstract class Discount {
    public abstract BigDecimal calculateDiscount(Order order);
    
    public BigDecimal calculateDiscount(Order order, BigDecimal amountAfterPreviousDiscounts) {
        return calculateDiscount(order);
    }
}
