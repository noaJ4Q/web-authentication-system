package com.example.userauthenticationsystem.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class User {
    
    @GetMapping("/home")
    public String homePage(){
        return "home1";
    }
}
