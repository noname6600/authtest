package com.learn.test.service.impl;

import com.learn.test.dto.OrderInfo;
import com.learn.test.dto.OrderResponse;
import com.learn.test.entity.Order;
import com.learn.test.enumerate.OrderStatus;
import com.learn.test.exception.BadCredentialsException;
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
    public OrderResponse createOrder(Long userId, OrderInfo orderInfo) {
        Order order = Order.builder()
                .product(orderInfo.getProduct())
                .status(OrderStatus.N)
                .deliveryDate(orderInfo.getDeliveryDate())
                .userId(userId)
                .build();
        orderRepository.save(order);
        return convertToResponse(order);
    }

    @Override
    public OrderResponse updateOrder(Long id, Long userId , OrderInfo orderInfo) {
        Order order = orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new BadCredentialsException("You do not own this order");
        }
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
    public Page<OrderResponse> getOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAllByUserId(userId, pageable);
        return orders.map(this::convertToResponse);
    }

    @Override
    public void deleteOrder(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new BadCredentialsException("You do not own this order");
        }
        orderRepository.delete(order);
    }


    private OrderResponse convertToResponse(Order order){
        return OrderResponse.builder()
                .iD(order.getId())
                .product(order.getProduct())
                .status(order.getStatus())
                .userId(order.getUserId())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus())
                .build();
    }

}
