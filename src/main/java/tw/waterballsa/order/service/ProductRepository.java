package tw.waterballsa.order.service;

import tw.waterballsa.order.domain.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductRepository {
    private Map<String, Product> products = new HashMap<>();

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public void clear() {
        products.clear();
    }
}
