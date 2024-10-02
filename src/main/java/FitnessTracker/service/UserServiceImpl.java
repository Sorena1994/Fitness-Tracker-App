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
    private JdbcTemplate jdbcTemplate; 

    @Autowired
    public UserServiceImpl(UserRepository theUserRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = theUserRepository;
        this.jdbcTemplate = jdbcTemplate; 
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByOrderByUsernameAsc();
    }

    @Override
    public User findById(String theUserID) { 
        Optional<User> result = userRepository.findById(theUserID); 
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
        createUserTable(theUser.getUsername()); 
    }

    @Override
    public void deleteById(String theId) { 
        userRepository.deleteById(theId);
        dropUserTable(theId);
    }
    private void createUserTable(String username) {
        validateUsername(username);
        String sql = "CREATE TABLE IF NOT EXISTS " + username + " ("
                + "id SERIAL PRIMARY KEY, "
                + "data VARCHAR(255));";
        jdbcTemplate.execute(sql);
    }

    private void dropUserTable(String username) {
        validateUsername(username);
        String sql = "DROP TABLE IF EXISTS " + username + ";";
        jdbcTemplate.execute(sql);
    }

    private void validateUsername(String username) {
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid username");
        }
    }
}
