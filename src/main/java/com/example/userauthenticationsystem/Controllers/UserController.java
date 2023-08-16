package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(method = RequestMethod.GET, value = "/home")
public class UserController {

    @Autowired
    UserRepository userRepository;

    private int id = 1;

    @GetMapping(value = {"", "/"})
    public String homePage(Model model){
        User user = userRepository.findById(id).get();

        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/edit")
    public String editPage(){
        return "profile-edit";
    }

    @GetMapping("/pass")
    public String passwordPage(){
        return "profile-pass";
    }
}
