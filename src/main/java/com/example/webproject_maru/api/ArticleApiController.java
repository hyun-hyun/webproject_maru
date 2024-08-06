package com.example.webproject_maru.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.service.ArticleService;

@RestController
@RequestMapping("/api/articles")
public class ArticleApiController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/{id}")
    public Article findByIdArticle(@PathVariable Long id){
        return articleService.findByIdArticle(id);
    }

    @GetMapping("/{id}/tags")
    public List<Map_a_t> getArticleTags(@PathVariable Long id){
        return articleService.getArticleTags(id);
    }
}
