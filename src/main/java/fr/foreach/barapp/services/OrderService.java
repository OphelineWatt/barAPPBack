package fr.foreach.barapp.services;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.foreach.barapp.dtos.OrderCreateRequest;
import fr.foreach.barapp.dtos.OrderItemRequest;
import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.entities.CocktailPrice;
import fr.foreach.barapp.entities.ItemStatus;
import fr.foreach.barapp.entities.Order;
import fr.foreach.barapp.entities.OrderItem;
import fr.foreach.barapp.entities.OrderStatus;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.OrderMapper;
import fr.foreach.barapp.repositories.CocktailPriceRepository;
import fr.foreach.barapp.repositories.CocktailRepository;
import fr.foreach.barapp.repositories.OrderItemRepository;
import fr.foreach.barapp.repositories.OrderRepository;
import fr.foreach.barapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CocktailRepository cocktailRepository;
    private final CocktailPriceRepository cocktailPriceRepository;
    private final OrderMapper orderMapper;

    public java.util.List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    public OrderResponse find(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        return orderMapper.toDto(order);
    }

    public void save(OrderCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.COMMANDEE);
        order.setPickupCode(UUID.randomUUID().toString().substring(0, 8));
        order.setTotalAmount(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            Cocktail cocktail = cocktailRepository.findById(itemReq.getCocktailId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cocktail not found " + itemReq.getCocktailId()));

            CocktailPrice price = cocktailPriceRepository
                    .findByCocktailIdAndSizeId(itemReq.getCocktailId(), itemReq.getSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Price not found for cocktail/size"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCocktail(cocktail);
            item.setSize(price.getSize());
            item.setQuantity(itemReq.getQuantity() == null ? 1 : itemReq.getQuantity());
            item.setItemStatus(ItemStatus.PREPARATION_INGREDIENTS);
            item.setUnitPrice(price.getPrice());

            BigDecimal itemTotal = price.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);

            order.getItems().add(item);
        }

        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    public void update(OrderCreateRequest request, Long id) {
        Order orderToUpdate = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found " + id));

        orderItemRepository.deleteAll(orderToUpdate.getItems());
        orderToUpdate.getItems().clear();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            Cocktail cocktail = cocktailRepository.findById(itemReq.getCocktailId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cocktail not found " + itemReq.getCocktailId()));

            CocktailPrice price = cocktailPriceRepository
                    .findByCocktailIdAndSizeId(itemReq.getCocktailId(), itemReq.getSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Price not found for cocktail/size"));

            OrderItem item = new OrderItem();
            item.setOrder(orderToUpdate);
            item.setCocktail(cocktail);
            item.setSize(price.getSize());
            item.setQuantity(itemReq.getQuantity() == null ? 1 : itemReq.getQuantity());
            item.setItemStatus(ItemStatus.PREPARATION_INGREDIENTS);
            item.setUnitPrice(price.getPrice());

            BigDecimal itemTotal = price.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);

            orderToUpdate.getItems().add(item);
        }

        orderToUpdate.setTotalAmount(total);
        orderRepository.save(orderToUpdate);
    }

    public void remove(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id " + id);
        }
        orderRepository.deleteById(id);
    }

    public void advanceItem(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found " + itemId));

        if (item.getOrder() == null || !item.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Item does not belong to order " + orderId);
        }

        ItemStatus current = item.getItemStatus();
        if (current == null) {
            throw new IllegalStateException("ItemStatus is null for item " + itemId);
        }

        ItemStatus[] values = ItemStatus.values();
        int idx = current.ordinal();
        if (idx >= values.length - 1) {
            // déjà au dernier état : on ne change rien
            return;
        }

        ItemStatus next = values[idx + 1];
        item.setItemStatus(next);
        orderItemRepository.save(item);
    }

}
