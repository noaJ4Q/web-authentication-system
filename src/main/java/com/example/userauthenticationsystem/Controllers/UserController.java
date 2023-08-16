package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    private int id = 1;

    @GetMapping(value = {"", "/"})
    public String homePage(Model model){
        model.addAttribute("user", userRepository.findById(id).get());
        return "home";
    }

    @GetMapping("/edit")
    public String editPage(Model model){
        model.addAttribute("user", userRepository.findById(id).get());
        return "profile-edit";
    }

    @PostMapping("/edit")
    public String saveUser(@Valid User user, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            System.out.println("Validation error: "+bindingResult);
            return "profile-edit";
        }
        else{
            userRepository.save(user);
            return "redirect:/user";
        }
    }

    @GetMapping("/pass")
    public String passwordPage(){
        return "profile-pass";
    }
}
