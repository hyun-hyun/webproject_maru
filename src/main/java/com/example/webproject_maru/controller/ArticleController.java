package com.example.webproject_maru.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.webproject_maru.dto.ArticleDetailDto;
import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.ReviewDto;
import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.dto.SubPicForm;
import com.example.webproject_maru.dto.TagCountDto;
import com.example.webproject_maru.dto.TagDto;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.SubPic;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.Map_r_tService;
import com.example.webproject_maru.service.ReviewService;


import lombok.extern.slf4j.Slf4j;


@Slf4j //로깅
@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private Map_r_tService map_r_tService;
    

    //게시글 생성
    @GetMapping("/write/article/anime")
    public String goNewAnime(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        List<String> defaultTags = Arrays.asList("깔끔한_작화", "느낌있는_작화", "매력적인_인물"); // 기본 태그 목록

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);
        model.addAttribute("defaultTags", defaultTags);

        return "articles/newAnime";
    }

    @PostMapping("/write/article/{category}/create")
    public String createArticle(ArticleForm form, @RequestParam("pic") MultipartFile[] mfile,
                                @RequestParam("realChar") String[] realChars,@RequestParam("realVoiceChar") String[] realVoiceChars,
                                @RequestParam("korChar") String[] korChars, @RequestParam("korVoiceChar")String[] korVoiceChars,
                                @PathVariable String category) {//폼 데이터를 DTO로 받기
        log.info(form.toString());
        SubPicForm[] subPicForms=new SubPicForm[realChars.length];
        for(int i=0;i<realChars.length;i++){
            if(realChars[i]!=null && !realChars[i].isEmpty()){
                subPicForms[i] =new SubPicForm(realChars[i],realVoiceChars[i],korChars[i],korVoiceChars[i]);
            }
            else{
                subPicForms[i]=new SubPicForm(null,null,null,null);
            }
        }
        log.info("서비스 create시작");
        Article saved=articleService.create(form, mfile, subPicForms, category);

        return "redirect:/articles/anime/"+saved.getId();
    }  
    //게시글 상세페이지
    @GetMapping("/articles/anime/{id}") //컨트롤러 변수{}, 뷰 변수{{}}
    public String show(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model){//매개변수로 url의 id받아오기
        log.info("id= "+id);//id잘 받았는지 확인
        //미로그인시 에러처리
        String nickname;
        Long member_id=null;
        if(userDetails!=null){
            nickname = userDetails.member.getNickname();
            member_id = userDetails.member.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);
        }else{
            //로그인 안된경우
            nickname="방문자";
        }
        

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        if(!email.equals("anonymousUser")){

            if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
                model.addAttribute("write", 1);
            }

            if(role.equals("ROLE_ADMIN")){
                model.addAttribute("admin",1);
            }
        }

        //1. id조회해서 데이터(entity, Optional<Article>) 가져오기
        Article articleEntity=articleService.findByIdArticle(id);

        //전체 리뷰 가져오기
        List<ReviewDto> reviewDtos=reviewService.reviews(id);
        //사용자 리뷰만 가져오기(미로그인 예외처리)
        ReviewForm myReview = (member_id != null) ?reviewService.my_review(id, member_id):null;
        //리뷰에서 사용된 태그(상세보기에서는 tagSelectionsCounts로 사용)
        List<String> usedTags=map_r_tService.getAllTagsByArticleId(id);
        //등록된 tag 가져오기
        List<String> allTags = articleService.getTagsByArticleId(id);
        //리뷰에서 선택되었던 tag만 tagName이랑 선택된 횟수 가져오기 
        List<TagCountDto> tagSelectionCounts = map_r_tService.countTagSelectionsByArticleId(id);
        
        ArticleDetailDto articleDetailDto=ArticleDetailDto.createArticleDetailDto(articleEntity, usedTags, allTags,tagSelectionCounts,reviewDtos,myReview);
        //2. 모델에 데이터 등록  
        //model.addAttribute("article", articleEntity);
        //model.addAttribute("reviewDtos", reviewDtos);
        //model.addAttribute("my_review", reviewForm);
        //model.addAttribute("tagList", tags);
        //model.addAttribute("tagSelectionCounts", tagSelectionCounts);
        model.addAttribute("article", articleDetailDto);

        //3. 뷰 페이지 반환
        return "articles/showAnime";
    }


    //게시글 수정
    @GetMapping("/write/article/{category}/{id}/edit")
    public String edit(@AuthenticationPrincipal CustomUserDetails userDetails, 
                        @PathVariable Long id, Model model){
        //header관련
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);

        //리뷰에서 사용된 태그(상세보기에서는 tagSelectionsCounts로 사용)
        List<String> usedTags=map_r_tService.getAllTagsByArticleId(id);
        //등록된 tag 가져오기
        List<String> allTags = articleService.getTagsByArticleId(id);

        Article articleEntity=articleService.findByIdArticle(id);
        ArticleDetailDto articleDetailDto=ArticleDetailDto.createArticleDetailDto(articleEntity, usedTags, allTags);

        //Article articleEntity=articleService.getArticleWithTags(id);
        model.addAttribute(("article"), articleDetailDto);

        return "articles/editAnime";
    }

    @PostMapping("/write/article/{category}/update")
    public String update(@PathVariable String category, ArticleForm articleForm,
                        @RequestParam("pic") MultipartFile[] newPic,
                        @RequestParam("realChar") String[] realChars,@RequestParam("realVoiceChar") String[] realVoiceChars,
                                @RequestParam("korChar") String[] korChars, @RequestParam("korVoiceChar")String[] korVoiceChars){
        log.info(articleForm.toString());
        SubPicForm[] subPicForms=new SubPicForm[5];
        for(int i=0;i<5;i++){
            if(realChars[i]!=null && !realChars[i].isEmpty()){
                subPicForms[i] =new SubPicForm(realChars[i],realVoiceChars[i],korChars[i],korVoiceChars[i]);
            }
            else{
                subPicForms[i]=new SubPicForm(null,null,null,null);
            }
        }
        Article updated=articleService.update(articleForm, newPic, subPicForms, category);
        //1. DTO->entity
        // Article articleEntity=articleForm.toEntity();
        //2. entity DB에 저장
        return "redirect:/articles/"+category+"/"+updated.getId();
    }

    //게시글 목록
    @GetMapping("/articles/anime")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        //미로그인시 에러처리
        String nickname;
        Long member_id=null;
        if(userDetails!=null){
            nickname = userDetails.member.getNickname();
            member_id = userDetails.member.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);
        }else{
            //로그인 안된경우
            nickname="방문자";
        }
        
         //등록한 게시물 내림차순(최신등록작품)
        //1. 모든 데이터 가져오기 list<entity>
        ArrayList<Article> articleEntityList=articleService.findArticlesDesc();
     
        // 각 Article의 태그 리스트 가져오기
        for (Article article : articleEntityList) {
            Long articleId = article.getId();
            List<String> usedTags = map_r_tService.getOnlyTagsByArticleId(articleId);  // List<String>으로 태그를 가져옴
            article.setUsedTags(usedTags);  // Article 엔티티에 태그 리스트를 추가
        }
        //2. 모델에 데이터 등록
        model.addAttribute("articleList", articleEntityList);


        //3. 뷰 페이지 설정
        return "articles/listAnime";
    }

    //게시글 삭제
    @PostMapping("/write/article/{category}/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String category, @PathVariable Long id, ArticleForm form,
                        @RequestParam(required = false) boolean admin, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        if(form.getId()!=id){
             return ResponseEntity.badRequest().body(Map.of("error", "해당 작품에 id정보 불일치가 발생했습니다."));
        }
        
        //manager권한에서는 리뷰가 있을 때는 작품삭제 불가
        boolean existingReview =reviewService.existsByArticleId(id);
        if(!admin && existingReview){
            //String str="/articles/anime/"+id;
            return ResponseEntity.badRequest().body(Map.of("error", "해당 작품에 리뷰가 있어 삭제가 불가합니다. 관리자권한으로 시도해주세요."));
            //return ResponseEntity.status(HttpStatus.FOUND)
                //.location(URI.create(str)) // 리다이렉트할 URL
                //.build();
                //.body(Map.of("message", "해당 작품에 리뷰가 있어 삭제가 불가합니다. 관리자권한으로 시도해주세요."));
        }
        //관리자권한일 경우 삭제 혹은 리뷰연관 없을경우 삭제(권한 더블체크)
        if((admin && role.equals("ROLE_ADMIN")) || (!reviewService.existsByArticleId(id)&&(role.equals("ROLE_MANAGER")||role.equals("ROLE_ADMIN")))){
                
            //태그, 리뷰, 사진, 기본 삭제
            articleService.delete(form, category, existingReview);
            return ResponseEntity.ok(Map.of("message", "삭제가 완료되었습니다."));
        }
        
        
        return ResponseEntity.badRequest().body(Map.of("error", "삭제할 수 없습니다."));
    }

    //게시글 3개월간 top 50
    @GetMapping("/articles/anime/top")
    public String top50(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        //미로그인시 에러처리
        String nickname;
        Long member_id=null;
        if(userDetails!=null){
            nickname = userDetails.member.getNickname();
            member_id = userDetails.member.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);
        }else{
            //로그인 안된경우
            nickname="방문자";
        }
        
       //3개월간 점수 높은 작품(점수순)
       //1. 50개 데이터 가져오기 list<entity>
       List<Article> highArticleEntityList=articleService.get3mRecentHighScoreArticles(50);
       // 각 Article의 태그 리스트 가져오기
       for (Article article : highArticleEntityList) {
           Long articleId = article.getId();
           List<String> usedTags = map_r_tService.getOnlyTagsByArticleId(articleId);  // List<String>으로 태그를 가져옴
           article.setUsedTags(usedTags);  // Article 엔티티에 태그 리스트를 추가
       }
       //2. 모델에 데이터 등록
       model.addAttribute("highArticleList", highArticleEntityList);

        //3. 뷰 페이지 설정
        return "articles/topAnime";
    }
}
