package com.example.webproject_maru.controller;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.dto.SubPicForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Review;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.ReviewService;


import lombok.extern.slf4j.Slf4j;


@Slf4j //로깅
@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/write/article/anime")
    public String goNewAnime(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String nickname = userDetails.member.getNickname();
        Long memgber_id = userDetails.member.getId();

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", memgber_id);


        return "articles/newAnime";
    }

    @PostMapping("/write/article/{catagory}/create")
    public String createArticle(ArticleForm form, @RequestParam("pic") MultipartFile[] mfile,
                                @RequestParam("realChar") String[] realChars,@RequestParam("realVoiceChar") String[] realVoiceChars,
                                @RequestParam("korChar") String[] korChars, @RequestParam("korVoiceChar")String[] korVoiceChars,
                                // @RequestParam("broad_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate broadDate,
                                @PathVariable String catagory) {//폼 데이터를 DTO로 받기
        log.info(form.toString());
        SubPicForm[] subPicForms=new SubPicForm[5];
        for(int i=0;i<5;i++){
            if(realChars[i]!=null && !realChars[i].isEmpty()){
                subPicForms[i] =new SubPicForm(realChars[i],realVoiceChars[i],korChars[i],korVoiceChars[i]);
            }
            else{
                subPicForms[i]=new SubPicForm(null,null,null,null);
            }
        }
        log.info("서비스 create시작");
        Article saved=articleService.create(form, mfile, subPicForms, catagory);
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
    public String show(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model){//매개변수로 url의 id받아오기
        log.info("id= "+id);//id잘 받았는지 확인

        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();
        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);

        //1. id조회해서 데이터(entity, Optional<Article>) 가져오기
        Article articleEntity=articleService.findByIdArticle(id);
        //전체 리뷰 가져오기
        List<ReviewForm> reviewDtos=reviewService.reviews(id);
        //사용자 리뷰만 가져오기
        ReviewForm reviewForm=reviewService.my_review(id, member_id);
        //2. 모델에 데이터 등록
        model.addAttribute("article", articleEntity);
        model.addAttribute("reviewDtos", reviewDtos);
        model.addAttribute("my_review", reviewForm);
        // model.addAttribute("commentDtos", commentsDtos);
        //3. 뷰 페이지 반환
        return "articles/showAnime";
    }


    @GetMapping("/articles/anime")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String nickname = userDetails.member.getNickname();
        model.addAttribute("nickname", nickname);

        //1. 모든 데이터 가져오기 list<entity>
        ArrayList<Article> articleEntityList=articleService.findArticles();
        //2. 모델에 데이터 등록
        model.addAttribute("articleList", articleEntityList);
        //3. 뷰 페이지 설정
        return "articles/listAnime";
    }

    
}
