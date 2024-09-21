package com.example.webproject_maru.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.SubPicForm;
import com.example.webproject_maru.dto.TagCountForm;
import com.example.webproject_maru.dto.TagForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.entity.SubPic;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.ArticleRepository;
import com.example.webproject_maru.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j //로깅
@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private Map_a_tService map_a_tService;
    @Autowired
    private Map_r_tService map_r_tService;


    @Value("${file.upload-dir}")
    private String uploadDir;

    //게시글 생성
    @Transactional
    public Article create(ArticleForm dto, MultipartFile[] files, SubPicForm[] subPicForms,String category) {
        Member member=memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 생성 실패!"+
                        "대상 회원이 없습니다."));//작성자 없으면 에러 메시지 출력
        Article article=dto.toEntity(member);
            if(article.getId() !=null) {
                    return null;
            }
        
        for (int i = 0; i < subPicForms.length; i++) {
            SubPicForm subPicForm = subPicForms[i];

            log.info("subPicForm밑");
            if(subPicForm != null){
                SubPic subPic = subPicForm.toEntity();
                log.info("subPicForm.toEntity");
                switch (i) {
                    case 0:
                        article.setSubPic1(subPic);
                        break;
                    case 1:
                        article.setSubPic2(subPic);
                        break;
                    case 2:
                        article.setSubPic3(subPic);
                        break;
                    case 3:
                        article.setSubPic4(subPic);
                        break;
                    case 4:
                        article.setSubPic5(subPic);
                        break;
                    default:
                        // 예외 처리나 로깅 등
                        break;
                }
            }
        }
        
        

        try{
        // article.setBroad_date(b_date);

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
                Path filePath = Paths.get(uploadDir+category + "/" + saveFileName);

                Files.createDirectories(filePath.getParent());
                files[i].transferTo(filePath);
                //files[i].transferTo(new File(uploadDir+genR+"_"+files[i].getOriginalFilename()));

                log.info(uploadDir+category + "/" + saveFileName);
                log.info("i="+i);

                switch(i){
                    case 0 : article.setMain_pic_name(files[0].getOriginalFilename());
                            article.setMain_pic(saveFileName);
                            break;
                    case 1 : article.getSubPic1().setName(files[1].getOriginalFilename());
                            article.getSubPic1().setPic(saveFileName);
                            break;
                    case 2 : article.getSubPic2().setName(files[2].getOriginalFilename());
                            article.getSubPic2().setPic(saveFileName);
                            break;
                    case 3 : article.getSubPic3().setName(files[3].getOriginalFilename());
                            article.getSubPic3().setPic(saveFileName);
                            break;
                    case 4 : article.getSubPic4().setName(files[4].getOriginalFilename());
                            article.getSubPic4().setPic(saveFileName);
                            break;
                    case 5 : article.getSubPic5().setName(files[5].getOriginalFilename());
                            article.getSubPic5().setPic(saveFileName);
                            break;
                }
            
            }

        }
        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        article.setAppendTime(SeoulNow);
        article.setUpdateTime(SeoulNow);
        }
        catch (IllegalStateException | IOException e){
        e.printStackTrace();
    }
    
    Article resultA=articleRepository.save(article);
        //태그(키워드) 생성 및 저장
        for (String tagName : dto.getTags()) {
            Tag tag = map_a_tService.findOrCreateTag(tagName);
            map_a_tService.saveMap_a_t(article, tag);
        }

        return resultA;
    }
/*
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
*/


    public ArrayList<Article> findArticles(){
        ArrayList<Article> articleEntityList=articleRepository.findAll();
        return articleEntityList;
    }
    
    public Article findByIdArticle(Long id){
        Article articleEntity=articleRepository.findById(id).orElse(null);
        return articleEntity;
    }

    //작품등록 최신순 정렬
    public ArrayList<Article> findArticlesDesc(){
        ArrayList<Article> articleEntityList=articleRepository.findAllByOrderByIdDesc();
        return articleEntityList;
    }
    
    //articleId별 tag조회
    public List<String> getTagsByArticleId(Long articleId){
        return map_a_tService.getTagsByArticleId(articleId);
    }

    public List<TagCountForm> countTagSelectionsByArticleId(Long articleId){
        return map_r_tService.countTagSelectionsByArticleId(articleId);
    }

    //작품수정시 ArticleForm+tag
    public Article getArticleWithTags(Long articleId){
        //ArticleForm articleForm=ArticleForm.createArticleForm(articleRepository.findById(articleId).orElse(null));
        Article articleEntity=articleRepository.findById(articleId).orElse(null);
        if(articleEntity!=null){
            List<String> tags=getTagsByArticleId(articleId);
            if(tags!=null){
                articleEntity.setAllTags(tags);
            }
            List<String> usedTags=map_r_tService.getOnlyAllTagsByArticleId(articleId);
            if(usedTags!=null){
                articleEntity.setUsedTags(usedTags);
            }
        }
        return articleEntity;
    }

    //수정데이터 반영
    public Article update(ArticleForm form, MultipartFile[] newFiles, SubPicForm[] subPicForms, String category){
        //1. Article 조회
        Article article=articleRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        //기존태그 리스트
        List<String> beforeTags=getTagsByArticleId(form.getId());
        //2. Article 수정
        //기본내용
        article.patch(form);
        //이미지
        try{
        for(int i=0;i<newFiles.length;i++){
            if(!newFiles[i].isEmpty()){
                log.info("기존이미지삭제 : ",article.getMain_pic());
                String beforeFilePath=uploadDir+category + "/"+article.getMain_pic();
                File beforeFile=new File(beforeFilePath);
                if(beforeFile.exists()){
                    beforeFile.delete();
                }

                // 새로운 이미지 저장
                UUID uuid = UUID.randomUUID();
                String oriName = newFiles[i].getOriginalFilename();
                String extension = oriName.substring(oriName.lastIndexOf("."));
                String saveFileName = uuid.toString() + extension;
                Path filePath = Paths.get(uploadDir + category + "/" + saveFileName);
                log.info("신규 이미지 저장 : ",saveFileName);
                Files.createDirectories(filePath.getParent());
                newFiles[i].transferTo(filePath);

                switch(i){
                    case 0 : article.setMain_pic_name(newFiles[0].getOriginalFilename());
                            article.setMain_pic(saveFileName);
                            break;
                    case 1 : article.getSubPic1().setName(newFiles[1].getOriginalFilename());
                            article.getSubPic1().setPic(saveFileName);
                            break;
                    case 2 : article.getSubPic2().setName(newFiles[2].getOriginalFilename());
                            article.getSubPic2().setPic(saveFileName);
                            break;
                    case 3 : article.getSubPic3().setName(newFiles[3].getOriginalFilename());
                            article.getSubPic3().setPic(saveFileName);
                            break;
                    case 4 : article.getSubPic4().setName(newFiles[4].getOriginalFilename());
                            article.getSubPic4().setPic(saveFileName);
                            break;
                    case 5 : article.getSubPic5().setName(newFiles[5].getOriginalFilename());
                            article.getSubPic5().setPic(saveFileName);
                            break;
                }                
            }
        }

        //기존태그중에 삭제한 태그 삭제
        for(String tag:beforeTags){
            if(!form.getTags().contains(tag)){
                map_a_tService.deleteByArticleIdAndTagName(form.getId(),tag);
            }
        }
        //새로 추가된 태그 저장
        for(String tag:form.getTags()){
            if(!beforeTags.contains(tag)){
                Tag newTag=map_a_tService.findOrCreateTag(tag);
                map_a_tService.saveMap_a_t(article, newTag);
            }
        }
        //수정시간 저장
        LocalDateTime SeoulNow=LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        article.setUpdateTime(SeoulNow);

        
    }catch(IllegalStateException | IOException e){
        e.printStackTrace();
    }

        //3. DB로 갱신
        Article updated=articleRepository.save(article);
        return updated;

    }

}
