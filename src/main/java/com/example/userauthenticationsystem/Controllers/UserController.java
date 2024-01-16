package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.Credentials;
import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.CredentialsRepository;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;

    public UserController(UserRepository userRepository,
                          CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }

    @GetMapping(value = {"", "/"})
    public String homePage(Model model, HttpSession session){

        //User user = getUserSession(session);
        //model.addAttribute("user", user);

        return "home";
    }

    @GetMapping("/edit")
    public String editPage(Model model, HttpSession session){

        User user = getUserSession(session);
        model.addAttribute("user", user);

        return "profile-edit";
    }

    @PostMapping("/edit")
    public String saveUser(@Valid User user, BindingResult bindingResult,
                           RedirectAttributes attributes,
                           HttpSession session){
        if (bindingResult.hasErrors()){
            System.out.println("Validation error: "+bindingResult);
            return "profile-edit";
        }
        else{

            User userSession = getUserSession(session);
            user.setRole(userSession.getRole());
            user.setState(userSession.getState());

            userRepository.save(user);
            updateCredentialsEmail(user);
            session.setAttribute("user", user);

            attributes.addFlashAttribute("toast", "Profile updated successfully");

            return "redirect:/user";
        }
    }

    @GetMapping("/pass")
    public String passwordPage(){
        return "profile-pass";
    }

    @PostMapping("/pass")
    public String savePassword(@RequestParam("pass") String oldPassword,
                               @RequestParam("newPass") String newPassword,
                               @RequestParam("confPass") String confPassword,
                               RedirectAttributes attributes,
                               HttpSession session){

        User user = getUserSession(session);
        if (oldPassword.isEmpty()){
            attributes.addFlashAttribute("pass", "Enter your current password");
            return "redirect:/user/pass";
        }
        if (!validOldPassword(user, oldPassword)){
            attributes.addFlashAttribute("pass", "Current password entered is incorrect");
            return "redirect:/user/pass";
        }
        if (!validNewPassword(newPassword, confPassword)){

            if (newPassword.isEmpty() || confPassword.isEmpty()){
                attributes.addFlashAttribute("newPass", "Enter your new password");
                attributes.addFlashAttribute("confPass", "Confirm your new password");
            }
            else if (!newPassword.equals(confPassword)){
                attributes.addFlashAttribute("matchPass", "Passwords must be equals");
            }

            return "redirect:/user/pass";
        }

        //updateCredentialsPassword(user, newPassword);

        attributes.addFlashAttribute("toast", "Password updated successfully");

        return "redirect:/user";
    }

    @GetMapping("/delete")
    public String deleteAccount(HttpSession session, HttpServletRequest request, HttpServletResponse response){

        User user = getUserSession(session);
        userRepository.deleteById(user.getId());
        credentialsRepository.deleteById(user.getId());

        logoutUser(request, response);

        return "redirect:/";
    }

    private User getUserSession(HttpSession session){
        return (User) session.getAttribute("user");
    }

    private void updateCredentialsEmail(User user){
        Credentials credentials = credentialsRepository.findById(user.getId()).get();
        credentials.setEmail(user.getEmail());
        credentialsRepository.save(credentials);
    }

    private void updateCredentialsPassword(User user, String newPassword){
        Credentials credentials = credentialsRepository.findById(user.getId()).get();
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        credentials.setPassword(encryptedPassword);
        credentialsRepository.save(credentials);
    }

    private boolean validOldPassword(User user, String oldPassword){
        Credentials credentials = credentialsRepository.findById(user.getId()).get();
        return new BCryptPasswordEncoder().matches(oldPassword, credentials.getPassword());
    }

    private boolean validNewPassword(String newPassword, String confPassword){
        return !newPassword.isEmpty() && !confPassword.isEmpty() && newPassword.equals(confPassword);
    }

    private void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        boolean isSecure = false;
        String contextPath = null;
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            isSecure = request.isSecure();
            contextPath = request.getContextPath();
        }
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);
        if (response != null) {
            Cookie cookie = new Cookie("JSESSIONID", null);
            String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
            cookie.setPath(cookiePath);
            cookie.setMaxAge(0);
            cookie.setSecure(isSecure);
            response.addCookie(cookie);
        }
    }
}
