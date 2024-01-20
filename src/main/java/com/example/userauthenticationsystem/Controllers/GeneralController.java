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
import org.springframework.web.bind.annotation.*;
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
    public String signupPage(@ModelAttribute("user") User user) {
        return "signup";
    }

    @PostMapping("/signup")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           RedirectAttributes attributes,
                           Model model,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword) {

        if (bindingResult.hasErrors() || !validPassword(password, confirmPassword)) {
            System.out.println("Validation error: " + bindingResult);
            if (!validPassword(password, confirmPassword)){
                model.addAttribute("password", "Enter a password");
                model.addAttribute("confirmPassword", "Confirm your password");
                System.out.println("Validations error: Invalid passwords");
            }
            return "signup";
        } else if (existUser(user.getEmail())){
            System.out.println("Validation error: Repeated user (email)");
            model.addAttribute("toast", "An account with the email '"+user.getEmail()+"' already exists");
            return "signup";
        } else {
            user.setState(1);
            user.setRole(0);
            userRepository.save(user);
            saveCredentials(user, password);

            attributes.addFlashAttribute("toast", true);
            attributes.addFlashAttribute("toast", "Account created successfully");
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

        if (!existUser(email)){
            attributes.addFlashAttribute("toast", "The email entered is invalid");
            return "redirect:/forgot";
        }
        if (!existToken(email)){
            // CREATE TOKEN AND SEND RESET PASS EMAIL
            Token token = generateToken(email);
            tokenRepository.save(token);
            sendResetPasswordEmail(request, email, token);
            attributes.addFlashAttribute("toast", "Reset password email sended to: "+email);
            return "redirect:/";
        }
        if (!expiredToken(email)){
            // ASK USER TO CHECK INBOX
            attributes.addFlashAttribute("toast", "A reset password email has already been sent, please check your inbox");
            return "redirect:/forgot";
        }
        // DELETE TOKEN AND RESEND RESET PASS EMAIL
        Token token = tokenRepository.findByUser(userRepository.findByEmail(email));
        token.setCode(UUID.randomUUID().toString());
        token.setExpirityDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);

        sendResetPasswordEmail(request, email, token);
        String toastText = "Recover email sended to: "+email;
        attributes.addFlashAttribute("toast", toastText);
        return "redirect:/";
    }

    @PostMapping("/recover")
    public String recoverAccount(@RequestParam("code") String code,
                                 @RequestParam("newPass") String newPassword,
                                 @RequestParam("confirmPass") String confirmPassword,
                                 RedirectAttributes attributes){

        if (validPassword(newPassword, confirmPassword)){
            updateCredentialsPassword(code, newPassword);
            deleteToken(code);

            attributes.addFlashAttribute("toast", "Your password has been successfully reset");
            return "redirect:/";
        }
        else{
            if (confirmPassword.isEmpty()){
                attributes.addFlashAttribute("confirmPassword", "Confirm your new password");
            }
            attributes.addFlashAttribute("newPassword", "Enter your new password");
            return "redirect:/forgot/"+code;
        }
    }

    private void sendResetPasswordEmail(HttpServletRequest request, String email, Token token){
        String link = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/forgot/"+token.getCode();
        String subject = "Recover account";
        String emailText = "Hi,\n\n" +
                "You have requested to reset your password. Please enter in the following lonk to restore your password (The link will be available only for 1 hour):\n" +
                link + "\n\n" +
                "Ignore this email if you do remeber your password, or you have not made this request.";

        emailService.sendEmail(email, subject, emailText);
    }

    private void updateCredentialsPassword(String code, String newPassword){
        Credentials credentials = credentialsRepository.findById(tokenRepository.findByCode(code).getUser().getId()).get();
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        credentials.setPassword(encryptedPassword);
        credentialsRepository.save(credentials);
    }

    private void deleteToken(String code){
        Token token = tokenRepository.findByCode(code);
        tokenRepository.delete(token);
    }

    private Token generateToken(String email){
        Token token = new Token();
        token.setCode(UUID.randomUUID().toString());
        token.setExpirityDate(LocalDateTime.now().plusHours(1));
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

    private boolean existUser(String email){
        User databaseUser = userRepository.findByEmail(email);
        return databaseUser != null;
    }

    private boolean existToken(String email){
        User user = userRepository.findByEmail(email);
        Token databaseToken = tokenRepository.findByUser(user);
        return databaseToken != null;
    }

    private boolean expiredToken(String email){
        User user = userRepository.findByEmail(email);
        Token databaseToken = tokenRepository.findByUser(user);
        return LocalDateTime.now().isAfter(databaseToken.getExpirityDate());
    }
}
