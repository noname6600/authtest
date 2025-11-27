package com.learn.test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learn.test.enumerate.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderInfo {

    @NotNull
    private OrderStatus status;

    @NotNull
    private String product;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate deliveryDate;



}
