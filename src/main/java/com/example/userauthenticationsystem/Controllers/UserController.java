package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.Credentials;
import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.CredentialsRepository;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    final
    UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;

    public UserController(UserRepository userRepository,
                          CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }

    @GetMapping(value = {"", "/"})
    public String homePage(Model model, HttpSession session){

        User user = getUserSession(session);
        model.addAttribute("user", user);

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
                           HttpSession session){
        if (bindingResult.hasErrors()){
            System.out.println("Validation error: "+bindingResult);
            return "profile-edit";
        }
        else{
            userRepository.save(user);
            updateCredentialsEmail(user);
            session.setAttribute("user", user);

            return "redirect:/user";
        }
    }

    @GetMapping("/pass")
    public String passwordPage(){
        return "profile-pass";
    }

    private User getUserSession(HttpSession session){
        return (User) session.getAttribute("user");
    }

    private void updateCredentialsEmail(User user){
        Credentials credentials = credentialsRepository.findById(user.getId()).get();
        credentials.setEmail(user.getEmail());
        credentialsRepository.save(credentials);
    }
}
