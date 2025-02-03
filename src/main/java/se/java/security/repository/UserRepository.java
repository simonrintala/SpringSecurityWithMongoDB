package se.java.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.java.security.models.User;

public interface UserRepository extends MongoRepository<User, String> {
}
