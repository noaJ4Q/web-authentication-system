package com.example.userauthenticationsystem.Controllers;

import com.example.userauthenticationsystem.Entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GeneralController {
    @GetMapping(value = {"", "/"})
    public String signinPage(){
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String saveUser(User user,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword){
        System.out.println(user.getFullname());
        System.out.println(user.getEmail());
        System.out.println(password);
        return "redirect:/";
    }

    @GetMapping(value = {"/forgot", "/forgot/{token}"})
    public String forgotPage(@PathVariable(name = "token", required = false) String token){
        return "forgot";
    }
}
