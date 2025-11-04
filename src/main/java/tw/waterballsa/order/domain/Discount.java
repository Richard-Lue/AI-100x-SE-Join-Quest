package tw.waterballsa.order.domain;

import java.math.BigDecimal;

public abstract class Discount {
    public abstract BigDecimal calculateDiscount(Order order);
}
