package com.learn.test.controller;

import com.learn.test.configuration.CustomUserDetails;
import com.learn.test.dto.ApiResponse;
import com.learn.test.dto.ApiResponseWithPage;
import com.learn.test.dto.OrderInfo;
import com.learn.test.dto.OrderResponse;
import com.learn.test.entity.User;
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderInfo orderInfo
    ) {
        User user = userDetails.getAccount().getUser();
        OrderResponse response = orderService.createOrder(user, orderInfo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponseWithPage<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = userDetails.getAccount().getUser();

        Page<OrderResponse> orders = orderService.getOrders(user, page, size);

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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody OrderInfo updateInfo
    ) {
        User user = userDetails.getAccount().getUser();

        if (!orderService.getOrderById(id).getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("You do not own this order");
        }

        OrderResponse response = orderService.updateOrder(id, updateInfo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        User user = userDetails.getAccount().getUser();
        if (!orderService.getOrderById(id).getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("You do not own this order");
        }
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted"));
    }
}
