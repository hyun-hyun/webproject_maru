package com.example.webproject_maru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {
    

    @GetMapping("/")
    public String goMain() {
        return "main";
    }

    @GetMapping("/login")
    public String goLogin(){
        return "members/login";
    }
    
    @GetMapping("/join")
    public String goJoin(){
        return "members/join";
    }
}
