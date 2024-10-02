package FitnessTracker.service;

import FitnessTracker.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(String theUserID); 
    User findByUserID(String theUserID); 
    void save(User theUser);
    void deleteById(String theUserID); 
    private void createUserTable(String username) {

    }
}
