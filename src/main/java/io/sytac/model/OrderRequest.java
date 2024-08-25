package io.sytac.model;

import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.ArrayList;
import java.util.List;
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    List<OrderItemRequest> orderItems = new ArrayList<>();
    public List<OrderItemRequest> getOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        return orderItems;
    }
}
