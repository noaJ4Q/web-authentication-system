package com.example.userauthenticationsystem.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(method = RequestMethod.GET, value = "/home")
public class User {
    
    @GetMapping(value = {"", "/"})
    public String homePage(){
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
