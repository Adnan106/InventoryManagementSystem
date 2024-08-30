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

    @PatchMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> deleteOrderItemQuantity(@PathVariable("orderId") Long orderId,
                                                        @PathVariable("itemId") Long itemId,
                                                        @RequestParam("quantity") int quantityToRemove) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (quantityToRemove <= 0) {
                throw new IllegalArgumentException("Quantity to remove must be greater than zero.");
            }

            orderService.deleteOrderItemQuantity(orderId, itemId, quantityToRemove);

            sample.stop(meterRegistry.timer("order_controller.delete_order_item_quantity.timer"));
            meterRegistry.counter("order_controller.delete_order_item_quantity.count").increment();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            meterRegistry.counter("order_controller.delete_order_item_quantity.errors").increment();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            meterRegistry.counter("order_controller.delete_order_item_quantity.errors").increment();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Order> orders = orderService.getAllOrders();
            sample.stop(meterRegistry.timer("order_controller.get_all_orders.timer"));
            meterRegistry.counter("order_controller.get_all_orders.count").increment();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            meterRegistry.counter("order_controller.get_all_orders.errors").increment();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid order ID");
            }
            Order order = orderService.getOrderById(id);
            sample.stop(meterRegistry.timer("order_controller.get_order_by_id.timer"));
            meterRegistry.counter("order_controller.get_order_by_id.count").increment();
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (ProductUnavailableException e) {
            meterRegistry.counter("order_controller.get_order_by_id.errors").increment();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            meterRegistry.counter("order_controller.get_order_by_id.errors").increment();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            meterRegistry.counter("order_controller.get_order_by_id.errors").increment();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
