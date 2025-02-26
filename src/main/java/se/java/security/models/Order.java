package se.java.security.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @NotNull(message = "Customer is required")
    @DBRef
    private User customer;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than 0")
    private Double totalAmount;

    @DBRef
    @NotNull(message = "Order must contain at least one product")
    private List<Product> items;

    private Map<String, Integer> quantities;


    public Order() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotNull(message = "Customer is required") User getCustomer() {
        return customer;
    }

    public void setCustomer(@NotNull(message = "Customer is required") User customer) {
        this.customer = customer;
    }


    public @NotNull(message = "Total amount is required") @Positive(message = "Total amount must be greater than 0") Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(@NotNull(message = "Total amount is required") @Positive(message = "Total amount must be greater than 0") Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public @NotNull(message = "Order must contain at least one product") List<Product> getItems() {
        return items;
    }

    public void setItems(@NotNull(message = "Order must contain at least one product") List<Product> items) {
        this.items = items;
    }

    public Map<String, Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(Map<String, Integer> quantities) {
        this.quantities = quantities;
    }
}