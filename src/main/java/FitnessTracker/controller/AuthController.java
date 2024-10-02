package FitnessTracker.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/oauth2/callback/google")
    public ModelAndView googleCallback(@AuthenticationPrincipal OAuth2User principal) {
        return new ModelAndView("redirect:/list-exercises");
    }

    @GetMapping("/oauth2/callback/github")
    public ModelAndView githubCallback(@AuthenticationPrincipal OAuth2User principal) {
        // You can process the user's info from the principal here if needed

        return new ModelAndView("redirect:/list-exercises");
    }
}
