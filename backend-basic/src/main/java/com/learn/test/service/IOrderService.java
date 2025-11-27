package com.learn.test.service;

import com.learn.test.dto.OrderInfo;
import com.learn.test.dto.OrderResponse;
import com.learn.test.entity.User;
import org.springframework.data.domain.Page;

public interface IOrderService {
    OrderResponse createOrder(User user, OrderInfo orderInfo);
    OrderResponse updateOrder(Long id, OrderInfo orderInfo);
    OrderResponse getOrderById(Long id);
    Page<OrderResponse> getOrders(User user, int page, int size);
    void deleteOrder(Long id);
}
