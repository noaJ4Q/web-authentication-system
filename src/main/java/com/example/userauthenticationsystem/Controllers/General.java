package com.example.userauthenticationsystem.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class General {
    @GetMapping("/")
    public String signinPage(){
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage(){
        return "signup";
    }

    @GetMapping("/forgot")
    public String forgotPage(){
        return "forgot";
    }
}
