package se.java.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.java.security.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}