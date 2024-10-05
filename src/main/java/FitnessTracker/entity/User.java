package FitnessTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private String userID; // Ensure this is lowercase to follow Java conventions

    @Column(name = "username")
    @NotNull(message = "is required")
    @Size(min = 6, message = "must be at least 6 characters long")
    private String username;

    @OneToMany(mappedBy = "userID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercise> exercises = new ArrayList<>();

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(String userID, String username, List<Exercise> exercises) {
        this.userID = userID;
        this.username = username;
        this.exercises = exercises;
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
