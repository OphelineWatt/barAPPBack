package fr.foreach.barapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.foreach.barapp.dtos.OrderCreateRequest;
import fr.foreach.barapp.dtos.OrderItemRequest;
import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderCreateRequest sampleRequest() {
        OrderItemRequest item = OrderItemRequest.builder().cocktailId(10L).sizeId(100L).quantity(2).build();
        return OrderCreateRequest.builder().userId(1L).items(List.of(item)).build();
    }

    @Test
    void createShouldReturn201() throws Exception {
        OrderCreateRequest request = sampleRequest();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(orderService, times(1)).save(any(OrderCreateRequest.class));
    }

    @Test
    void createShouldReturn404WhenUserNotFound() throws Exception {
        OrderCreateRequest request = sampleRequest();
        doThrow(new ResourceNotFoundException("User not found 1")).when(orderService).save(any(OrderCreateRequest.class));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShouldReturn200WithBody() throws Exception {
        OrderResponse response = OrderResponse.builder().id(1L).userId(1L).status("COMMANDEE").build();
        when(orderService.find(1L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMMANDEE"));
    }

    @Test
    void getShouldReturn404WhenNotFound() throws Exception {
        when(orderService.find(99L)).thenThrow(new ResourceNotFoundException("Order not found with id 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void advanceItemShouldReturn204() throws Exception {
        mockMvc.perform(post("/api/orders/1/advance-item/500"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).advanceItem(1L, 500L);
    }

    @Test
    void advanceItemShouldReturn400WhenItemDoesNotBelongToOrder() throws Exception {
        doThrow(new IllegalArgumentException("Item does not belong to order 1"))
                .when(orderService).advanceItem(1L, 500L);

        mockMvc.perform(post("/api/orders/1/advance-item/500"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturn204() throws Exception {
        OrderCreateRequest request = sampleRequest();

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).update(eq(request), eq(1L));
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).remove(1L);
    }
}
