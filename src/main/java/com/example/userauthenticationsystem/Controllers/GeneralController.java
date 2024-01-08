package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.Credentials;
import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GeneralController {

    private final UserRepository userRepository;

    public GeneralController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = {"", "/"})
    public String signinPage(){
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String saveUser(@Valid User user, BindingResult bindingResult,
                           Model model,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword){

        boolean validPasswords = validatePassword(password, confirmPassword);

        if (bindingResult.hasErrors() && !validPasswords){
            System.out.println("Validation error: "+bindingResult);
            return "signup";
        }
        else{
            model.addAttribute("signup", true);
            userRepository.save(user);
            saveCredentials(user, password);
            return "redirect:/";
        }

    }

    @GetMapping(value = {"/forgot", "/forgot/{token}"})
    public String forgotPage(@PathVariable(name = "token", required = false) String token){
        return "forgot";
    }

    private void saveCredentials(User user, String password){
        Credentials credentials = new Credentials();
        String encryptedPassword = new BCryptPasswordEncoder().encode(password);
    }

    private boolean validatePassword(String password, String confirmPassword){
        return !password.equals("") && !confirmPassword.equals("") && password.equals(confirmPassword);
    }
}
