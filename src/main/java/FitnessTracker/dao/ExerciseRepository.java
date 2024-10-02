package FitnessTracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import FitnessTracker.entity.Exercise;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {


    public List<Exercise> findAllByOrderByExerciseNameAsc();

}
