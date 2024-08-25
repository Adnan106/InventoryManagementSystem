package io.sytac.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    String productName;
    int quantity;
    double totalPrice;
    double price;
}
