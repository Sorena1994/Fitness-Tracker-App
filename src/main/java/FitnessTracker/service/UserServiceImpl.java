package FitnessTracker.service;

import java.util.List;
import java.util.Optional;

import FitnessTracker.dao.UserRepository;
import FitnessTracker.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private JdbcTemplate jdbcTemplate; // Add JdbcTemplate to manage database operations

    @Autowired
    public UserServiceImpl(UserRepository theUserRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = theUserRepository;
        this.jdbcTemplate = jdbcTemplate; // Initialize JdbcTemplate
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByOrderByUsernameAsc();
    }

    @Override
    public User findById(String theUserID) { // Change Long to String
        Optional<User> result = userRepository.findById(theUserID); // Change Long to String
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new RuntimeException("Did not find user id - " + theUserID);
        }
    }

    @Override
    public User findByUserID(String userID) {
        return userRepository.findByUserID(userID);
    }

    @Override
    public void save(User theUser) {
        userRepository.save(theUser);
        createUserTable(theUser.getUsername()); // Create a user-specific table after saving
    }

    @Override
    public void deleteById(String theId) { // Change Long to String
        userRepository.deleteById(theId);
        dropUserTable(theId); // Optional: Drop the user table when deleting the user
    }

    // Method to create a dynamic table for the user
    private void createUserTable(String username) {
        // Validate the username to prevent SQL injection
        validateUsername(username);

        // SQL statement to create a table for the user
        String sql = "CREATE TABLE IF NOT EXISTS " + username + " ("
                + "id SERIAL PRIMARY KEY, "
                + "data VARCHAR(255));"; // Customize the table structure as needed
        jdbcTemplate.execute(sql);
    }

    // Method to drop the user table (optional)
    private void dropUserTable(String username) {
        validateUsername(username);
        String sql = "DROP TABLE IF EXISTS " + username + ";";
        jdbcTemplate.execute(sql);
    }

    // Validate username to prevent SQL injection
    private void validateUsername(String username) {
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid username");
        }
    }
}
