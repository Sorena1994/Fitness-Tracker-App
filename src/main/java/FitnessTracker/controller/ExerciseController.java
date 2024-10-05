package FitnessTracker.controller;

import java.security.Principal;
import java.util.List;

import FitnessTracker.entity.User;
import FitnessTracker.service.ExerciseService;
import FitnessTracker.service.UserService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import FitnessTracker.entity.Exercise;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {

	private final ExerciseService exerciseService;
	private final UserService userService;

	public ExerciseController(ExerciseService theExerciseService, UserService theUserService) {
		this.exerciseService = theExerciseService;
		this.userService = theUserService;
	}

	// Add mapping for "/list"
	@GetMapping("/list")
	public String listExercise(Model theModel) {
		List<Exercise> theExercises = exerciseService.findAll();
		theModel.addAttribute("exercises", theExercises);
		return "exercises/list-exercises";
	}

	@GetMapping("/AddExercise")
	public String showFormForAdd(@ModelAttribute("exercise") Exercise exercise, Principal principal, Model theModel) {
		String userID = principal.getName(); // Assuming principal name is the userID
		exercise.setUserID(userID); // Associate user with exercise
		theModel.addAttribute("exercise", exercise);
		return "exercises/exercise-form";
	}

	@GetMapping("/UpdateExercise")
	public String showFormForUpdate(@RequestParam("exerciseId") Integer theId, Model theModel) { // Change Long to Integer
		Exercise theExercise = exerciseService.findById(theId); // Directly use Integer
		theModel.addAttribute("exercise", theExercise);
		return "exercises/exercise-form";
	}

	@PostMapping("/delete")
	public String deleteExercise(@RequestParam("exerciseId") Integer theId) { // Change Long to Integer
		exerciseService.deleteById(theId); // Directly use Integer
		return "redirect:/exercises/list";
	}

	@PostMapping("/save")
	public String saveExercise(@ModelAttribute("exercise") @Valid Exercise theExercise, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "exercises/exercise-form"; // Return to form if there are validation errors
		}
		exerciseService.save(theExercise);
		return "redirect:/exercises/list";
	}
}
