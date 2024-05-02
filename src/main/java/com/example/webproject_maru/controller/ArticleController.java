package com.example.webproject_maru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArticleController {
    @GetMapping("/write/anime")
    public String goNewAnime(){
        return "articles/newAnime";
    }
}
