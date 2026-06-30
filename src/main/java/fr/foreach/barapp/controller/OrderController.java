package fr.foreach.barapp.controller;

import fr.foreach.barapp.dtos.OrderCreateRequest;
import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Validated OrderCreateRequest request) {
        orderService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.find(id));
    }

    @PostMapping("/{orderId}/advance-item/{itemId}")
    public ResponseEntity<Void> advanceItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        orderService.advanceItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Validated OrderCreateRequest request) {
        orderService.update(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
