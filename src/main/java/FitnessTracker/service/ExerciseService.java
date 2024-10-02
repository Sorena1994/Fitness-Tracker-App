package FitnessTracker.service;

import java.util.List;

import FitnessTracker.entity.Exercise;

public interface ExerciseService {

    List<Exercise> findAll();

    Exercise findById(int theId);

    void save(Exercise theExercise);

    void deleteById(int theId);

}
