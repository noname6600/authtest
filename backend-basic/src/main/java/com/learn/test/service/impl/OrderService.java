package com.learn.test.service.impl;

import com.learn.test.dto.OrderInfo;
import com.learn.test.dto.OrderResponse;
import com.learn.test.entity.Order;
import com.learn.test.entity.User;
import com.learn.test.enumerate.OrderStatus;
import com.learn.test.exception.ResourceNotFoundException;
import com.learn.test.repository.OrderRepository;
import com.learn.test.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(User user, OrderInfo orderInfo) {
        Order order = Order.builder()
                .product(orderInfo.getProduct())
                .status(OrderStatus.N)
                .deliveryDate(orderInfo.getDeliveryDate())
                .user(user)
                .build();
        orderRepository.save(order);
        return convertToResponse(order);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderInfo orderInfo) {
        Order order = orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order not found"));
        order.setDeliveryDate(orderInfo.getDeliveryDate());
        order.setProduct(orderInfo.getProduct());
        order.setStatus(orderInfo.getStatus());
        orderRepository.save(order);
        return convertToResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long id){
        Order order = orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order not found"));
        return convertToResponse(order);
    }

    @Override
    public Page<OrderResponse> getOrders(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);
        return orders.map(this::convertToResponse);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        orderRepository.delete(order);
    }


    private OrderResponse convertToResponse(Order order){
        return OrderResponse.builder()
                .iD(order.getId())
                .product(order.getProduct())
                .status(order.getStatus())
                .userId(order.getUser().getId())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus())
                .build();
    }

}
