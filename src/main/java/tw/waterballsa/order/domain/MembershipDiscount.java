package tw.waterballsa.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MembershipDiscount extends Discount {
    private Customer.MembershipLevel membershipLevel;

    public MembershipDiscount(Customer.MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public Customer.MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    @Override
    public BigDecimal calculateDiscount(Order order) {
        if (order.getCustomer().getMembershipLevel() != membershipLevel) {
            return BigDecimal.ZERO;
        }

        BigDecimal originalAmount = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        double rate = 1.0 - membershipLevel.getDiscountRate();
        return originalAmount.multiply(BigDecimal.valueOf(rate))
                .setScale(0, RoundingMode.HALF_UP);
    }
}
