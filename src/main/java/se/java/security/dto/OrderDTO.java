package se.java.security.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderDTO {
    @NotNull(message = "Customer id is required")
    private String customerId;

    @NotNull(message = "Order must contain at least one item")
    private List<OrderItemDTO> items;


    public OrderDTO() {
    }

    public @NotNull(message = "Customer id is required") String getCustomerId() {
        return customerId;
    }

    public @NotNull(message = "Order must contain at least one item") List<OrderItemDTO> getItems() {
        return items;
    }

    public void setCustomerId(@NotNull(message = "Customer id is required") String customerId) {
        this.customerId = customerId;
    }

    public void setItems(@NotNull(message = "Order must contain at least one item") List<OrderItemDTO> items) {
        this.items = items;
    }
}