package com.learn.test.controller;

import com.learn.test.dto.ApiResponse;
import com.learn.test.dto.ApiResponseWithPage;
import com.learn.test.dto.OrderInfo;
import com.learn.test.dto.OrderResponse;
import com.learn.test.exception.ResourceNotFoundException;
import com.learn.test.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal String userId,
            @RequestBody OrderInfo orderInfo
    ) {
        OrderResponse response = orderService.createOrder(Long.parseLong(userId), orderInfo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponseWithPage<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<OrderResponse> orders = orderService.getOrders(Long.parseLong(userId), page, size);

        return ResponseEntity.ok(
                ApiResponseWithPage.success(
                        orders.getContent(),
                        page,
                        size,
                        orders.getTotalPages()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id
    ) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @AuthenticationPrincipal String userId,
            @PathVariable Long id,
            @RequestBody OrderInfo updateInfo
    ) {


        OrderResponse response = orderService.updateOrder(id,Long.parseLong(userId) ,updateInfo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(
            @AuthenticationPrincipal String userId,
            @PathVariable Long id
    ) {
        orderService.deleteOrder(id, Long.parseLong(userId));
        return ResponseEntity.ok(ApiResponse.success("Order deleted"));
    }
}
