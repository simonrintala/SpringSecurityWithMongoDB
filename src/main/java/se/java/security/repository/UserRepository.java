package se.java.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.java.security.models.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
