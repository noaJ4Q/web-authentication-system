package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.Credentials;
import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.CredentialsRepository;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import com.example.userauthenticationsystem.Services.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class GeneralController {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final EmailService emailService;
    public GeneralController(UserRepository userRepository,
                             CredentialsRepository credentialsRepository,
                             EmailService emailService) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.emailService = emailService;
    }

    @GetMapping(value = {"", "/"})
    public String signinPage() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String saveUser(@Valid User user, BindingResult bindingResult,
                           RedirectAttributes attributes,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword) {

        boolean validPasswords = validatePassword(password, confirmPassword);

        if (bindingResult.hasErrors() && !validPasswords) {
            System.out.println("Validation error: " + bindingResult);
            return "signup";
        } else {
            user.setState(1);
            user.setRole(0);
            userRepository.save(user);
            saveCredentials(user, password);

            attributes.addFlashAttribute("toast", true);
            attributes.addFlashAttribute("toastText", "Account created successfully");
            return "redirect:/";
        }

    }

    @GetMapping(value = {"/forgot", "/forgot/{token}"})
    public String forgotPage(@PathVariable(name = "token", required = false) String token) {

        return "forgot";
    }

    @PostMapping("/forgot")
    public String receiveEmail(@RequestParam("email") String email,
                               RedirectAttributes attributes) {

        String toastText;
        String subject = "Recover account";
        String emailText = "Hi,\n" +
                "You have requested to reset your password. Please enter in the following lonk to restore your passsword:\n" +
                "Ignore this email if you do remeber your password, or you have not made this request.";

        emailService.sendEmail(email, subject, emailText);
        toastText = "Email sended to: "+email;
        //toastText = "Error sending email to: "+email;

        attributes.addFlashAttribute("toast", true);
        attributes.addFlashAttribute("toastText", toastText);
        return "redirect:/";
    }

    private void saveCredentials(User user, String password) {
        Credentials credentials = new Credentials();
        String encryptedPassword = new BCryptPasswordEncoder().encode(password);

        credentials.setUser(user);
        credentials.setEmail(user.getEmail());
        credentials.setPassword(encryptedPassword);

        credentialsRepository.save(credentials);
    }

    private boolean validatePassword(String password, String confirmPassword) {
        return !password.equals("") && !confirmPassword.equals("") && password.equals(confirmPassword);
    }
}
