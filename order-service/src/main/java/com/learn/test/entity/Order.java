package com.learn.test.entity;

import com.learn.test.enumerate.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity{
    private String product;

    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Long userId;

}
