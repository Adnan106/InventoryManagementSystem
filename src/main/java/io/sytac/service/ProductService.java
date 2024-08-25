package io.sytac.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.sytac.model.Product;
import io.sytac.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductService {
    ProductRepository productRepository;
    MeterRegistry meterRegistry;

    public List<Product> getAllProducts() {
        meterRegistry.counter("product_service.get_all_products.count").increment();
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        meterRegistry.counter("product_service.get_product_by_id.count").increment();
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        meterRegistry.counter("product_service.add_product.count").increment();
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        if (productRepository.existsById(id)) {
            product.setId(id);
            meterRegistry.counter("product_service.update_product.count").increment();
            return productRepository.save(product);
        } else {
            meterRegistry.counter("product_service.update_product.errors").increment();
            return null;
        }
    }

    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
            meterRegistry.counter("product_service.delete_product.count").increment();
        } catch (Exception e) {
            meterRegistry.counter("product_service.delete_product.errors").increment();
        }
    }
}
