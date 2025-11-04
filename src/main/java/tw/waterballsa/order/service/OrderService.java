package tw.waterballsa.order.service;

import tw.waterballsa.order.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderService handles order creation and discount calculation.
 * Note: This service is not thread-safe. Create separate instances for concurrent use.
 */
public class OrderService {
    private List<Discount> productDiscounts = new ArrayList<>();
    private List<Discount> orderDiscounts = new ArrayList<>();

    public void addDiscount(Discount discount) {
        if (discount instanceof ProductPercentageDiscount || 
            discount instanceof BuyNGetOneFreeDiscount || 
            discount instanceof BundleDiscount) {
            productDiscounts.add(discount);
        } else {
            orderDiscounts.add(discount);
        }
    }

    public void clearDiscounts() {
        productDiscounts.clear();
        orderDiscounts.clear();
    }

    public Order createOrder(Customer customer, List<OrderItem> items) {
        Order order = new Order(customer);
        items.forEach(order::addItem);
        
        calculateOrderAmount(order);
        
        return order;
    }

    private void calculateOrderAmount(Order order) {
        // Calculate original amount
        BigDecimal originalAmount = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setOriginalAmount(originalAmount);

        // Apply product-level discounts first
        BigDecimal productDiscountAmount = productDiscounts.stream()
                .map(discount -> discount.calculateDiscount(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate amount after product discounts
        BigDecimal amountAfterProductDiscount = originalAmount.subtract(productDiscountAmount);

        // Apply order-level discounts on the amount after product discounts
        BigDecimal orderDiscountAmount = orderDiscounts.stream()
                .map(discount -> discount.calculateDiscount(order, amountAfterProductDiscount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = productDiscountAmount.add(orderDiscountAmount);
        order.setDiscountAmount(totalDiscount);

        // Calculate final amount
        BigDecimal finalAmount = originalAmount.subtract(totalDiscount);
        order.setTotalAmount(finalAmount);
    }
}
