package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.Credentials;
import com.example.userauthenticationsystem.Entities.Token;
import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.CredentialsRepository;
import com.example.userauthenticationsystem.Repositories.TokenRepository;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import com.example.userauthenticationsystem.Services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class GeneralController {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    public GeneralController(UserRepository userRepository,
                             CredentialsRepository credentialsRepository,
                             EmailService emailService,
                             TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
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

        boolean validPasswords = validPassword(password, confirmPassword);

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

    @GetMapping(value = {"/forgot", "/forgot/",  "/forgot/{code}"})
    public String forgotPage(@PathVariable(name = "code", required = false) String code,
                             Model model) {
        if (code != null){
            if (validToken(code)){
                model.addAttribute("code", code);
                return "recover";
            }
            else{
                return "invalidToken";
            }
        }
        else{
            return "forgot";
        }
    }

    @PostMapping("/forgot")
    public String receiveEmail(@RequestParam("email") String email,
                               RedirectAttributes attributes,
                               HttpServletRequest request) {

        Token token = generateToken(email);
        tokenRepository.save(token);

        String toastText;
        String link = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/forgot/"+token.getCode();
        String subject = "Recover account";
        String emailText = "Hi,\n" +
                "You have requested to reset your password. Please enter in the following lonk to restore your password (The link will be available only for 3 hours):\n" +
                link + "\n\n" +
                "Ignore this email if you do remeber your password, or you have not made this request.";

        emailService.sendEmail(email, subject, emailText);
        toastText = "Email sended to: "+email;
        //toastText = "Error sending email to: "+email;

        attributes.addFlashAttribute("toast", true);
        attributes.addFlashAttribute("toastText", toastText);
        return "redirect:/";
    }

    @PostMapping("/recover")
    public String recoverAccount(@RequestParam("code") String code,
                                 @RequestParam("newPass") String newPassword,
                                 @RequestParam("confirmPass") String confirmPassword,
                                 RedirectAttributes attributes){

        System.out.println("user code: "+code);

        if (validPassword(newPassword, confirmPassword)){
            updateCredentialsPassword(code, newPassword);

            attributes.addFlashAttribute("toast", true);
            attributes.addFlashAttribute("toastText", "Your password has been successfully reset");
            return "redirect:/";
        }
        else{
            return "redirect:/forgot/"+code;
        }
    }

    private void updateCredentialsPassword(String code, String newPassword){
        Credentials credentials = credentialsRepository.findById(tokenRepository.findByCode(code).getId()).get();
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        credentials.setPassword(encryptedPassword);
        credentialsRepository.save(credentials);
    }

    private Token generateToken(String email){
        Token token = new Token();
        token.setCode(UUID.randomUUID().toString());
        token.setExpirityDate(LocalDateTime.now().plusHours(3));
        token.setUser(userRepository.findByEmail(email));
        return token;
    }
    private void saveCredentials(User user, String password) {
        Credentials credentials = new Credentials();
        String encryptedPassword = new BCryptPasswordEncoder().encode(password);

        credentials.setUser(user);
        credentials.setEmail(user.getEmail());
        credentials.setPassword(encryptedPassword);

        credentialsRepository.save(credentials);
    }

    private boolean validToken(String code){
        Token token = tokenRepository.findByCode(code);
        if (token != null){
            return token.getExpirityDate().isAfter(LocalDateTime.now());
        }
        else{
            return false;
        }
    }

    private boolean validPassword(String password, String confirmPassword) {
        return !password.isEmpty() && !confirmPassword.isEmpty() && password.equals(confirmPassword);
    }
}
