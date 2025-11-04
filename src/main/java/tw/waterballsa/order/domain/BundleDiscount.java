package tw.waterballsa.order.domain;

import java.math.BigDecimal;
import java.util.Set;

public class BundleDiscount extends Discount {
    private Set<String> productIds;
    private BigDecimal bundlePrice;

    public BundleDiscount(Set<String> productIds, BigDecimal bundlePrice) {
        this.productIds = productIds;
        this.bundlePrice = bundlePrice;
    }

    @Override
    public BigDecimal calculateDiscount(Order order) {
        boolean hasAllProducts = productIds.stream()
                .allMatch(productId -> order.getItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(productId)));
        
        if (hasAllProducts) {
            BigDecimal originalTotal = order.getItems().stream()
                    .filter(item -> productIds.contains(item.getProduct().getId()))
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            return originalTotal.subtract(bundlePrice);
        }
        
        return BigDecimal.ZERO;
    }
}
