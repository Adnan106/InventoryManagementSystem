package io.sytac.controller;
import io.sytac.exception.ProductUnavailableException;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.sytac.model.Invoice;
import io.sytac.model.Order;
import io.sytac.model.OrderRequest;
import io.sytac.model.Product;
import io.sytac.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    OrderService orderService;
    MeterRegistry meterRegistry;


    @PostMapping
    public ResponseEntity<Invoice> createOrder(@RequestBody OrderRequest orderRequest) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {

            if(orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty() )
            {
                throw new IllegalArgumentException("Invalid Request");

            }
            Invoice createdOrder = orderService.createOrder(orderRequest);
            sample.stop(meterRegistry.timer("order_controller.create_order.timer"));
            meterRegistry.counter("order_controller.create_order.count").increment();
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (ProductUnavailableException | IllegalArgumentException e) {
            meterRegistry.counter("order_controller.create_order.errors").increment();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            meterRegistry.counter("order_controller.create_order.errors").increment();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/{id}/total-price")
    public double getTotalPriceOfOrder(@PathVariable("id") Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            double totalPrice = orderService.getTotalPriceOfOrder(id);
            sample.stop(meterRegistry.timer("order_controller.get_total_price_of_order.timer"));
            meterRegistry.counter("order_controller.get_total_price_of_order.count").increment();
            return totalPrice;
        } catch (Exception e) {
            meterRegistry.counter("order_controller.get_total_price_of_order.errors").increment();
            throw e;
        }
    }
}
