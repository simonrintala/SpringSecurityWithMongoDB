package se.java.security.dto;

public class OrderItemDTO {
    // vilken produkt
    private String productId;

    // hur många an produkten har man köpt
    private int quantity;

    public OrderItemDTO() {
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}