package com.example.webproject_maru.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Article create(ArticleForm dto, MultipartFile[] files,String catagory) {
        Article article=dto.toEntity();
        if(article.getId() !=null) {
            return null;
        }
        

        try{

        
        for(int i=0;i<files.length;i++){
            if(!files[i].isEmpty()){
                log.info("저장시작");
                UUID uuid=UUID.randomUUID();
                String ori_name=files[i].getOriginalFilename();
                //확장자 추출
                String extention = ori_name.substring(ori_name.lastIndexOf("."));
                // 랜덤 id 값 + 파일 확장자
                String saveFileName = uuid.toString() + extention;
                // 파일을 서버에 저장
                Path filePath = Paths.get(uploadDir+catagory + "/" + saveFileName);

                // Random random=new Random();
                // String genR=random.nextInt(10000)+"";
                
                //파일을 서버에 저장
                // Path filePath = Paths.get(uploadDir+catagory+"/"+genR+"_"+files[i].getOriginalFilename());
                Files.createDirectories(filePath.getParent());
                files[i].transferTo(filePath);
                //files[i].transferTo(new File(uploadDir+genR+"_"+files[i].getOriginalFilename()));

                log.info(uploadDir+catagory + "/" + saveFileName);
                log.info("i="+i);

                switch(i){
                    case 0 : article.setMain_pic_name(files[0].getOriginalFilename());
                            article.setMain_pic(saveFileName);
                            break;
                    case 1 : article.setSub_pic1_name(files[1].getOriginalFilename());
                            article.setSub_pic1(saveFileName);
                            break;
                    case 2 : article.setSub_pic2_name(files[2].getOriginalFilename());
                            article.setSub_pic2(saveFileName);
                            break;
                    case 3 : article.setSub_pic1_name(files[3].getOriginalFilename());
                            article.setSub_pic1(saveFileName);
                            break;
                    case 4 : article.setSub_pic4_name(files[4].getOriginalFilename());
                            article.setSub_pic4(saveFileName);
                            break;
                    case 5 : article.setSub_pic5_name(files[5].getOriginalFilename());
                            article.setSub_pic5(saveFileName);
                            break;
                }
            
            }

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
