package FitnessTracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FitnessTracker.dao.ExerciseRepository;
import FitnessTracker.entity.Exercise;

@Service
public class ExerciseServiceImpl implements ExerciseService {

	private ExerciseRepository exerciseRepository;
	
	@Autowired
	public ExerciseServiceImpl(ExerciseRepository theExerciseRepository) {
		exerciseRepository = theExerciseRepository;
	}
	
	@Override
	public List<Exercise> findAll() {
		return exerciseRepository.findAllByOrderByExerciseNameAsc();
	}

	@Override
	public Exercise findById(int theId) {
		Optional<Exercise> result = exerciseRepository.findById(theId);

		Exercise theExercise = null;
		
		if (result.isPresent()) {
			theExercise = result.get();
		}
		else {
			// we didn't find the exercise
			throw new RuntimeException("Did not find exercise id - " + theId);
		}
		
		return theExercise;
	}


	@Override
	public void save(Exercise theExercise) {
		exerciseRepository.save(theExercise);
	}

	@Override
	public void deleteById(int theId) {
		exerciseRepository.deleteById(theId);
	}

}






