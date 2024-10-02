package FitnessTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Table(name = "exercise")
public class Exercise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exercise_id")
	private Integer id;

	@NotNull
	@Column(name = "exercise_name")
	private String exerciseName;

	@NotNull
	@Column(name = "user_id")
	private String userID;

	@NotNull
	@Column(name = "weight")
	private double weight;

	@NotNull
	@Column(name = "date")
	private LocalDate date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExerciseName() {
		return exerciseName;
	}

	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Exercise{" +
				"id=" + id +
				", exerciseName='" + exerciseName + '\'' +
				", userID='" + userID + '\'' +
				", weight=" + weight +
				'}';
	}
}
