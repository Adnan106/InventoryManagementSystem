package io.sytac.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.sytac.model.Product;
import io.sytac.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    ProductService productService;
    MeterRegistry meterRegistry;

    @GetMapping
    public List<Product> getAllProducts() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Product> products = productService.getAllProducts();
            sample.stop(meterRegistry.timer("product_controller.get_all_products.timer"));
            meterRegistry.counter("product_controller.get_all_products.count").increment();
            return products;
        } catch (Exception e) {
            meterRegistry.counter("product_controller.get_all_products.errors").increment();
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable("id") Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Optional<Product> product = productService.getProductById(id);
            sample.stop(meterRegistry.timer("product_controller.get_product_by_id.timer"));
            meterRegistry.counter("product_controller.get_product_by_id.count").increment();
            return product;
        } catch (Exception e) {
            meterRegistry.counter("product_controller.get_product_by_id.errors").increment();
            throw e;
        }
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Product addedProduct = productService.addProduct(product);
            sample.stop(meterRegistry.timer("product_controller.add_product.timer"));
            meterRegistry.counter("product_controller.add_product.count").increment();
            return addedProduct;
        } catch (Exception e) {
            meterRegistry.counter("product_controller.add_product.errors").increment();
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            sample.stop(meterRegistry.timer("product_controller.update_product.timer"));
            meterRegistry.counter("product_controller.update_product.count").increment();
            return updatedProduct;
        } catch (Exception e) {
            meterRegistry.counter("product_controller.update_product.errors").increment();
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            productService.deleteProduct(id);
            sample.stop(meterRegistry.timer("product_controller.delete_product.timer"));
            meterRegistry.counter("product_controller.delete_product.count").increment();
        } catch (Exception e) {
            meterRegistry.counter("product_controller.delete_product.errors").increment();
            throw e;
        }
    }
}
