package FitnessTracker.dao;

import FitnessTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> { // Change Long to String
    List<User> findAllByOrderByUsernameAsc();
    User findByUserID(String userID); // Change String return type to User
}
