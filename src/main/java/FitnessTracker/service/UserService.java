package FitnessTracker.service;

import FitnessTracker.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(String theUserID); // Change Long to String
    User findByUserID(String theUserID); // Change return type to User
    void save(User theUser);
    void deleteById(String theUserID); // Change Long to String

    // New method to create a dynamic table for the user
    private void createUserTable(String username) {

    }
}
