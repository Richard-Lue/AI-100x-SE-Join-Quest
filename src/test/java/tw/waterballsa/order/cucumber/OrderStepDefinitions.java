package tw.waterballsa.order.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.而且;
import io.cucumber.datatable.DataTable;
import tw.waterballsa.order.domain.*;
import tw.waterballsa.order.service.OrderService;
import tw.waterballsa.order.service.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderStepDefinitions {
    private ProductRepository productRepository;
    private OrderService orderService;
    private Order currentOrder;
    private Customer currentCustomer;

    @Before
    public void setUp() {
        productRepository = new ProductRepository();
        orderService = new OrderService();
    }

    @假設("系統中有以下商品:")
    public void 系統中有以下商品(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String id = row.get("商品編號");
            String name = row.get("商品名稱");
            BigDecimal price = new BigDecimal(row.get("單價"));
            Product product = new Product(id, name, price);
            productRepository.addProduct(product);
        }
    }

    @假設("系統有滿額折扣優惠: 滿 {int} 元折 {int} 元")
    public void 系統有滿額折扣優惠(int threshold, int discountAmount) {
        orderService.addDiscount(new AmountThresholdDiscount(
                BigDecimal.valueOf(threshold),
                BigDecimal.valueOf(discountAmount)
        ));
    }

    @假設("商品 {string} 有 {int} 折優惠")
    public void 商品有折扣優惠(String productId, int discountPercentage) {
        double discountRate = discountPercentage / 10.0;
        orderService.addDiscount(new ProductPercentageDiscount(productId, discountRate));
    }

    @假設("商品 {string} 有買二送一優惠")
    public void 商品有買二送一優惠(String productId) {
        orderService.addDiscount(new BuyNGetOneFreeDiscount(productId, 2));
    }

    @假設("商品組合 {string} 和 {string} 有優惠價 {int} 元")
    public void 商品組合有優惠價(String productId1, String productId2, int bundlePrice) {
        orderService.addDiscount(new BundleDiscount(
                Set.of(productId1, productId2),
                BigDecimal.valueOf(bundlePrice)
        ));
    }

    @假設("客戶 {string} 是 {string} 會員，享有 {int} 折優惠")
    public void 客戶是會員享有折扣優惠(String customerName, String memberLevel, int discountPercentage) {
        Customer.MembershipLevel level = "VIP".equals(memberLevel) 
                ? Customer.MembershipLevel.VIP 
                : Customer.MembershipLevel.REGULAR;
        currentCustomer = new Customer(customerName, level);
        orderService.addDiscount(new MembershipDiscount(level));
    }

    @當("客戶 {string} 建立訂單，包含以下商品:")
    public void 客戶建立訂單(String customerName, DataTable dataTable) {
        if (currentCustomer == null || !currentCustomer.getName().equals(customerName)) {
            currentCustomer = new Customer(customerName);
        }

        List<OrderItem> items = new ArrayList<>();
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String productId = row.get("商品編號");
            int quantity = Integer.parseInt(row.get("數量"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            items.add(new OrderItem(product, quantity));
        }

        currentOrder = orderService.createOrder(currentCustomer, items);
    }

    @那麼("訂單總金額應為 {int} 元")
    public void 訂單總金額應為(int expectedAmount) {
        assertEquals(BigDecimal.valueOf(expectedAmount), currentOrder.getTotalAmount(),
                "訂單總金額不符");
    }

    @那麼("訂單原始金額應為 {int} 元")
    public void 訂單原始金額應為(int expectedAmount) {
        assertEquals(BigDecimal.valueOf(expectedAmount), currentOrder.getOriginalAmount(),
                "訂單原始金額不符");
    }

    @而且("訂單折扣金額應為 {int} 元")
    public void 訂單折扣金額應為(int expectedAmount) {
        assertEquals(BigDecimal.valueOf(expectedAmount), currentOrder.getDiscountAmount(),
                "訂單折扣金額不符");
    }

    @而且("訂單狀態應為 {string}")
    public void 訂單狀態應為(String expectedStatus) {
        assertEquals(expectedStatus, currentOrder.getStatus().getDisplayName(),
                "訂單狀態不符");
    }
}
