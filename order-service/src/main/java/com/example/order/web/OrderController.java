package com.example.order.web;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repo.OrderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    public record OrderItemRequest(Long productId, @Min(1) int quantity, @Min(0) double price) {}
    public record CreateOrderRequest(@NotEmpty List<OrderItemRequest> items, @Min(0) double totalAmount) {}

    @PostMapping
    public ResponseEntity<Order> create(@RequestHeader("X-User-Id") Long userId,
                                        @Valid @RequestBody CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("CREATED");
        order.setTotalAmount(request.totalAmount());

        for (OrderItemRequest i : request.items()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(i.productId());
            oi.setQuantity(i.quantity());
            oi.setPrice(i.price());
            order.getItems().add(oi);
        }

        Order saved = repository.save(order);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Order> list(@RequestHeader("X-User-Id") Long userId) {
        return repository.findAll().stream().filter(o -> o.getUserId().equals(userId)).toList();
    }
}


