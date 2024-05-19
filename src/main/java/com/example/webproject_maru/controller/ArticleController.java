package com.example.webproject_maru.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.service.ArticleService;

import lombok.extern.slf4j.Slf4j;


@Slf4j //로깅
@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/write/article/anime")
    public String goNewAnime(){
        return "articles/newAnime";
    }

    @PostMapping("/write/article/anime/create")
    public String createArticle(ArticleForm form, @RequestParam("main_pic") MultipartFile[] mfile) {//폼 데이터를 DTO로 받기
        log.info(form.toString());

        Article saved=articleService.create(form, mfile);
        /*
        //System.out.println(form.toString());//DTO에 잘 담겼는지 확인        
        //1. DTO -> entity
        Article article = form.toEntity();
        log.info(article.toString());
        //System.out.println(article.toString());//엔티티 변환확인
        //2. save entity in repository(DB)
        Article saved=articleRepository.save(article);
        log.info(saved.toString());
        //System.out.println(saved.toString());//DB저장확인
*/

        return "redirect:/articles/anime/"+saved.getId();
    }

    @GetMapping("/articles/anime/{id}") //컨트롤러 변수{}, 뷰 변수{{}}
    public String show(@PathVariable Long id, Model model){//매개변수로 url의 id받아오기
        log.info("id= "+id);//id잘 받았는지 확인
        //1. id조회해서 데이터(entity, Optional<Article>) 가져오기
        Article articleEntity=articleService.findByIdArticle(id);
        //2. 모델에 데이터 등록
        model.addAttribute("article", articleEntity);
        // model.addAttribute("commentDtos", commentsDtos);
        //3. 뷰 페이지 반환
        return "articles/showAnime";
    }

    @GetMapping("/articles/anime")
    public String index(Model model){
        //1. 모든 데이터 가져오기 list<entity>
        ArrayList<Article> articleEntityList=articleService.findArticles();
        //2. 모델에 데이터 등록
        model.addAttribute("articleList", articleEntityList);
        //3. 뷰 페이지 설정
        return "articles/listAnime";
    }

}
