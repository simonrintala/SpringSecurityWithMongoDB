package se.java.security.dto;

import java.util.List;

public class OrderResponse {
    private String userId;
    private List<String> orderedProductIds;

    public OrderResponse() {
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getOrderedProductIds() {
        return orderedProductIds;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOrderedProductIds(List<String> orderedProductIds) {
        this.orderedProductIds = orderedProductIds;
    }
}