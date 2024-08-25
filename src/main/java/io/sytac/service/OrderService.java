package io.sytac.service;
import io.sytac.exception.ProductUnavailableException;

import io.micrometer.core.instrument.MeterRegistry;
import io.sytac.model.*;
import io.sytac.repository.OrderRepository;
import io.sytac.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {
    OrderRepository orderRepository;
    MeterRegistry meterRegistry;
    ProductRepository productRepository;



    @Transactional
    public Invoice createOrder(OrderRequest orderRequest) {
        Order order=new Order();
        Invoice invoice = new Invoice();
        double totalOrderPrice = 0;
        int totalOrderItems = 0;

        for (OrderItemRequest orderItemRequest : orderRequest.getOrderItems()) {
            Long id=orderItemRequest.getProductId();
            int quantity= orderItemRequest.getQuantity();
            if (id == null || quantity <= 0 ) {
                throw new IllegalArgumentException("Invalid Request");
            }
            Optional<Product> productOpt = productRepository.findById(id);

            if (productOpt.isEmpty()) {
                throw new ProductUnavailableException("Product with ID " + id + " is not available.");
            }

            Product product = productOpt.get();

            if (quantity > product.getQuantity()) {
                throw new ProductUnavailableException("Requested quantity for product " + product.getName() + " is not available.");
            }

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            double totalOrderItemPrice = product.getPrice()*quantity;
            OrderItem item = new OrderItem(product, quantity, totalOrderItemPrice);
            order.getItems().add(item);
            OrderItemResponse orderItemResponse = new OrderItemResponse(product.getName(), quantity, product.getPrice(), totalOrderItemPrice);
            invoice.getOrderItems().add(orderItemResponse);
            totalOrderPrice += item.getProduct().getPrice() * item.getQuantity();
            totalOrderItems += item.getQuantity();
        }

        order.setTotalPrice(totalOrderPrice);
        order.setTotalItems(totalOrderItems);
        order = orderRepository.save(order);
        invoice.setOrderId(order.getId());
        invoice.setTotalPrice(totalOrderPrice);
        invoice.setTotalQuantity(totalOrderItems);
        return invoice;
    }


    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public double getTotalPriceOfOrder(Long id) {
        Optional<Order> order = getOrderById(id);
        return order.map(Order::getTotalPrice).orElse(0.0);
    }

}
