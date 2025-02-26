package se.java.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.java.security.models.Order;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerId(String customerId);
}