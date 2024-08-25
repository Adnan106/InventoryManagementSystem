package io.sytac.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    Long orderId;
    int totalQuantity;
    double totalPrice;
    List<OrderItemResponse> orderItems = new ArrayList<>();
    public List<OrderItemResponse> getOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        return orderItems;
    }
}
