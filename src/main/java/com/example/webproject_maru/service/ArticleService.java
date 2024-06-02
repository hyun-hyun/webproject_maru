package com.example.webproject_maru.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.repository.ArticleRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j //로깅
@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public Article create(ArticleForm dto, MultipartFile[] files) {
        Article article=dto.toEntity();
        if(article.getId() !=null) {
            return null;
        }
        

        try{
        for(int i=0;i<files.length;i++){
            log.info("저장시작");
            Random random=new Random();
            String genR=random.nextInt(10000)+"";
            String pic_anime_path="C:/Users/LG/Desktop/CodeStudy/Java/webproject_maru/src/main/resources/static/images/pic/anime/";

            //파일을 서버에 저장
            files[i].transferTo(new File(pic_anime_path+genR+"_"+files[i].getOriginalFilename()));
            log.info(genR+"_"+files[i].getOriginalFilename());
            article.setMain_pic(genR+"_"+files[i].getOriginalFilename());

        }
        }
        catch (IllegalStateException | IOException e){
        e.printStackTrace();
    }
        

        return articleRepository.save(article);
    }

    @Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
        //1. dto 묶음 -> entity 묶음 변환
        List<Article> articleList=dtos.stream()
                    .map(dto->dto.toEntity())
                    .collect(Collectors.toList());
        //2. entity묶음 -> DB 저장
        articleList.stream()
                .forEach(article->articleRepository.save(article));
        //3. 강제로 에러 발생시키기
        articleRepository.findById(-1L)
                .orElseThrow(()->new IllegalArgumentException("결제 실패!"));
        //4. 결과 반환
        return articleList;
    }

    public ArrayList<Article> findArticles(){
        ArrayList<Article> articleEntityList=articleRepository.findAll();
        return articleEntityList;
    }
    
    public Article findByIdArticle(Long id){
        Article articleEntity=articleRepository.findById(id).orElse(null);
        return articleEntity;
    }
}
