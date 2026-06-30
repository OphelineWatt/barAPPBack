package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.OrderCreateRequest;
import fr.foreach.barapp.dtos.OrderItemRequest;
import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.entities.CocktailPrice;
import fr.foreach.barapp.entities.ItemStatus;
import fr.foreach.barapp.entities.Order;
import fr.foreach.barapp.entities.OrderItem;
import fr.foreach.barapp.entities.Size;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.OrderMapper;
import fr.foreach.barapp.repositories.CocktailPriceRepository;
import fr.foreach.barapp.repositories.CocktailRepository;
import fr.foreach.barapp.repositories.OrderItemRepository;
import fr.foreach.barapp.repositories.OrderRepository;
import fr.foreach.barapp.repositories.UserRepository;
import fr.foreach.barapp.services.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CocktailRepository cocktailRepository;

    @Mock
    private CocktailPriceRepository cocktailPriceRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cocktail testCocktail;
    private Size testSize;
    private CocktailPrice testPrice;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder().id(1L).email("client@example.com").build();
        testCocktail = Cocktail.builder().id(10L).name("Mojito").build();
        testSize = Size.builder().id(100L).code("L").build();
        testPrice = CocktailPrice.builder()
                .cocktailId(10L)
                .sizeId(100L)
                .price(new BigDecimal("8.50"))
                .size(testSize)
                .build();

        testOrder = Order.builder().id(1L).user(testUser).build();
    }

    @Test
    @DisplayName("findAll should return list of mapped orders")
    void testFindAll() {
        OrderResponse response = OrderResponse.builder().id(1L).build();
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));
        when(orderMapper.toDto(testOrder)).thenReturn(response);

        List<OrderResponse> result = orderService.findAll();

        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("find should return order when found")
    void testFindSuccess() {
        OrderResponse response = OrderResponse.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toDto(testOrder)).thenReturn(response);

        OrderResponse result = orderService.find(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("find should throw ResourceNotFoundException when not found")
    void testFindNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.find(99L));
    }

    @Test
    @DisplayName("save should throw ResourceNotFoundException when user not found")
    void testSaveUserNotFound() {
        OrderCreateRequest request = OrderCreateRequest.builder().userId(99L).items(List.of()).build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("save should throw ResourceNotFoundException when cocktail not found")
    void testSaveCocktailNotFound() {
        OrderItemRequest itemReq = OrderItemRequest.builder().cocktailId(10L).sizeId(100L).quantity(2).build();
        OrderCreateRequest request = OrderCreateRequest.builder().userId(1L).items(List.of(itemReq)).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cocktailRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("save should throw ResourceNotFoundException when price not found")
    void testSavePriceNotFound() {
        OrderItemRequest itemReq = OrderItemRequest.builder().cocktailId(10L).sizeId(100L).quantity(2).build();
        OrderCreateRequest request = OrderCreateRequest.builder().userId(1L).items(List.of(itemReq)).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cocktailRepository.findById(10L)).thenReturn(Optional.of(testCocktail));
        when(cocktailPriceRepository.findByCocktailIdAndSizeId(10L, 100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("save should build order with items, default quantity and computed total")
    void testSaveSuccess() {
        OrderItemRequest itemWithQty = OrderItemRequest.builder().cocktailId(10L).sizeId(100L).quantity(2).build();
        OrderItemRequest itemWithoutQty = OrderItemRequest.builder().cocktailId(10L).sizeId(100L).quantity(null).build();
        OrderCreateRequest request = OrderCreateRequest.builder()
                .userId(1L)
                .items(List.of(itemWithQty, itemWithoutQty))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cocktailRepository.findById(10L)).thenReturn(Optional.of(testCocktail));
        when(cocktailPriceRepository.findByCocktailIdAndSizeId(10L, 100L)).thenReturn(Optional.of(testPrice));

        orderService.save(request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(captor.capture());
        Order saved = captor.getValue();

        assertEquals(2, saved.getItems().size());
        assertEquals(new BigDecimal("25.50"), saved.getTotalAmount());
        for (OrderItem item : saved.getItems()) {
            assertEquals(ItemStatus.PREPARATION_INGREDIENTS, item.getItemStatus());
        }
        assertEquals(2, saved.getItems().get(0).getQuantity());
        assertEquals(1, saved.getItems().get(1).getQuantity());
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when order not found")
    void testUpdateNotFound() {
        OrderCreateRequest request = OrderCreateRequest.builder().userId(1L).items(List.of()).build();
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.update(request, 99L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should delete old items, rebuild items and recompute total")
    void testUpdateSuccess() {
        OrderItem oldItem = OrderItem.builder().id(500L).order(testOrder).build();
        testOrder.getItems().add(oldItem);

        OrderItemRequest itemReq = OrderItemRequest.builder().cocktailId(10L).sizeId(100L).quantity(3).build();
        OrderCreateRequest request = OrderCreateRequest.builder().userId(1L).items(List.of(itemReq)).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(cocktailRepository.findById(10L)).thenReturn(Optional.of(testCocktail));
        when(cocktailPriceRepository.findByCocktailIdAndSizeId(10L, 100L)).thenReturn(Optional.of(testPrice));

        orderService.update(request, 1L);

        verify(orderItemRepository, times(1)).deleteAll(anyList());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(captor.capture());
        Order saved = captor.getValue();

        assertEquals(1, saved.getItems().size());
        assertEquals(new BigDecimal("25.50"), saved.getTotalAmount());
    }

    @Test
    @DisplayName("remove should delete order when it exists")
    void testRemoveSuccess() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.remove(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("remove should throw ResourceNotFoundException when order not found")
    void testRemoveNotFound() {
        when(orderRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orderService.remove(99L));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("advanceItem should throw ResourceNotFoundException when item not found")
    void testAdvanceItemNotFound() {
        when(orderItemRepository.findById(500L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.advanceItem(1L, 500L));
    }

    @Test
    @DisplayName("advanceItem should throw IllegalArgumentException when item does not belong to order")
    void testAdvanceItemWrongOrder() {
        Order otherOrder = Order.builder().id(2L).build();
        OrderItem item = OrderItem.builder().id(500L).order(otherOrder).itemStatus(ItemStatus.PREPARATION_INGREDIENTS).build();
        when(orderItemRepository.findById(500L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> orderService.advanceItem(1L, 500L));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("advanceItem should throw IllegalStateException when itemStatus is null")
    void testAdvanceItemNullStatus() {
        OrderItem item = OrderItem.builder().id(500L).order(testOrder).itemStatus(null).build();
        when(orderItemRepository.findById(500L)).thenReturn(Optional.of(item));

        assertThrows(IllegalStateException.class, () -> orderService.advanceItem(1L, 500L));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("advanceItem should move item to next status and persist it")
    void testAdvanceItemSuccess() {
        OrderItem item = OrderItem.builder().id(500L).order(testOrder).itemStatus(ItemStatus.PREPARATION_INGREDIENTS).build();
        when(orderItemRepository.findById(500L)).thenReturn(Optional.of(item));

        orderService.advanceItem(1L, 500L);

        assertEquals(ItemStatus.ASSEMBLAGE, item.getItemStatus());
        verify(orderItemRepository, times(1)).save(item);
    }

    @Test
    @DisplayName("advanceItem should do nothing when item is already in the last status")
    void testAdvanceItemAlreadyLastStatus() {
        OrderItem item = OrderItem.builder().id(500L).order(testOrder).itemStatus(ItemStatus.TERMINEE).build();
        when(orderItemRepository.findById(500L)).thenReturn(Optional.of(item));

        orderService.advanceItem(1L, 500L);

        assertEquals(ItemStatus.TERMINEE, item.getItemStatus());
        verify(orderItemRepository, never()).save(any());
    }
}
