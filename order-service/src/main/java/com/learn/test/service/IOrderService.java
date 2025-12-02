package com.learn.test.service;

import com.learn.test.dto.OrderInfo;
import com.learn.test.dto.OrderResponse;
import org.springframework.data.domain.Page;

public interface IOrderService {
    OrderResponse createOrder(Long userId, OrderInfo orderInfo);
    OrderResponse updateOrder(Long id, Long userId, OrderInfo orderInfo);
    OrderResponse getOrderById(Long id);
    Page<OrderResponse> getOrders(Long userId, int page, int size);
    void deleteOrder(Long id, Long userId);
}
