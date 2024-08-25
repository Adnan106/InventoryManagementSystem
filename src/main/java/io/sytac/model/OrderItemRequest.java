package io.sytac.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
    Long productId;
    int quantity;
}
