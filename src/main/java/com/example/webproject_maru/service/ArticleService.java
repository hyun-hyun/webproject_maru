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
    @Autowired
    private TagService tagService;
    @Autowired
    private ReviewService reviewService;


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
        createSubPic(subPicForms, article);
        
        try{
            saveImage(files, category, article);
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
    @Transactional
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
        Article updating=updateSubPic(subPicForms, article);
        //이미지
        try{
            updateImage(newFiles, category, updating);

            //기존태그중에 삭제한 태그 삭제
            for(String tag:beforeTags){
                if(!form.getTags().contains(tag)){
                    map_a_tService.deleteByArticleIdAndTagName(form.getId(),tag);

                    //해당태그가 다른 article_id에서 사용중인지 확인
                    Long tagId=tagService.findByTag(tag).getId();
                    log.info("삭제중 tagId: {}",tagId);
                    log.info("개수: {}", map_a_tService.countByTagId(tagId));
                    if(map_a_tService.countByTagId(tagId)==0){//기타 연결 없는경우
                        tagService.deleteById(tagId);
                    }
                }
            }
            //새로 추가된 태그 저장
            for(String tag:form.getTags()){
                if(!beforeTags.contains(tag)){
                    Tag newTag=map_a_tService.findOrCreateTag(tag);
                    map_a_tService.saveMap_a_t(updating, newTag);
                }
            }
            //수정시간 저장
            LocalDateTime SeoulNow=LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            updating.setUpdateTime(SeoulNow);

            
        }catch(IllegalStateException | IOException e){
            e.printStackTrace();
        }

        //3. DB로 갱신
        Article updated=articleRepository.save(updating);
        return updated;

    }

    

    //게시글 삭제
    @Transactional
    public void delete(ArticleForm form, String category, boolean existingReview){
        //삭제대상 가져오기
        Article article=findByIdArticle(form.getId());

        if(article!=null){
            Long articleId=article.getId();
            //태그삭제(map_a_t, tag)
            deleteMap_a_tAndTagByArticleId(form.getId());
            //리뷰삭제
            if(existingReview){
                reviewService.deleteByArticleId(articleId);
            }
            //사진삭제
            try{
                deleteImage(category, article);
                }catch(IllegalStateException e){
                e.printStackTrace();
            }
            //기본삭제
            articleRepository.delete(article);
        }
    }

    //게시글삭제관련
    public void deleteMap_a_tAndTagByArticleId(Long articleId){
        List<String> tagName=map_a_tService.getTagsByArticleId(articleId);
        map_a_tService.deleteByArticleId(articleId);
        //해당태그가 다른 article_id에서 미사용중일시 tag삭제
        for(String tag:tagName){
            Long tagId=tagService.findByTag(tag).getId();
            if(map_a_tService.countByTagId(tagId)==0){//기타 연결 없는경우
                tagService.deleteById(tagId);
            }
        }
        
    }

    private void createSubPic(SubPicForm[] subPicForms, Article article) {
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
    }
    
    private Article updateSubPic(SubPicForm[] subPicForms, Article article) {
        for (int i = 0; i < subPicForms.length; i++) {
            SubPicForm subPicForm = subPicForms[i];

            log.info("subPicForm밑");
            if(subPicForm != null){
                //SubPic subPic = subPicForm.toEntity();
                String realChar=subPicForm.getRealChar();
                String realVoiceChar=subPicForm.getRealVoiceChar();
                String korChar=subPicForm.getKorChar();
                String korVoiceChar=subPicForm.getKorVoiceChar();
                log.info("subPicForm.toEntity");
                switch (i) {
                    case 0:
                        if(article.getSubPic1().getRealChar()!=realChar){
                            article.getSubPic1().setRealChar(realChar);
                        }
                        if(article.getSubPic1().getRealVoiceChar()!=realVoiceChar){
                            article.getSubPic1().setRealVoiceChar(realVoiceChar);
                        }
                        if(article.getSubPic1().getKorChar()!=korChar){
                            article.getSubPic1().setKorChar(korChar);
                        }
                        if(article.getSubPic1().getKorVoiceChar()!=korVoiceChar){
                            article.getSubPic1().setKorVoiceChar(korVoiceChar);
                        }
                        break;
                    case 1:
                        if(article.getSubPic2().getRealChar()!=realChar){
                            article.getSubPic2().setRealChar(realChar);
                        }
                        if(article.getSubPic2().getRealVoiceChar()!=realVoiceChar){
                            article.getSubPic2().setRealVoiceChar(realVoiceChar);
                        }
                        if(article.getSubPic2().getKorChar()!=korChar){
                            article.getSubPic2().setKorChar(korChar);
                        }
                        if(article.getSubPic2().getKorVoiceChar()!=korVoiceChar){
                            article.getSubPic2().setKorVoiceChar(korVoiceChar);
                        }
                        break;
                    case 2:
                        if(article.getSubPic3().getRealChar()!=realChar){
                            article.getSubPic3().setRealChar(realChar);
                        }
                        if(article.getSubPic3().getRealVoiceChar()!=realVoiceChar){
                            article.getSubPic3().setRealVoiceChar(realVoiceChar);
                        }
                        if(article.getSubPic3().getKorChar()!=korChar){
                            article.getSubPic3().setKorChar(korChar);
                        }
                        if(article.getSubPic3().getKorVoiceChar()!=korVoiceChar){
                            article.getSubPic3().setKorVoiceChar(korVoiceChar);
                        }
                        break;
                    case 3:
                        if(article.getSubPic4().getRealChar()!=realChar){
                            article.getSubPic4().setRealChar(realChar);
                        }
                        if(article.getSubPic4().getRealVoiceChar()!=realVoiceChar){
                            article.getSubPic4().setRealVoiceChar(realVoiceChar);
                        }
                        if(article.getSubPic4().getKorChar()!=korChar){
                            article.getSubPic4().setKorChar(korChar);
                        }
                        if(article.getSubPic4().getKorVoiceChar()!=korVoiceChar){
                            article.getSubPic4().setKorVoiceChar(korVoiceChar);
                        }
                        break;
                    case 4:
                        if(article.getSubPic5().getRealChar()!=realChar){
                            article.getSubPic5().setRealChar(realChar);
                        }
                        if(article.getSubPic5().getRealVoiceChar()!=realVoiceChar){
                            article.getSubPic5().setRealVoiceChar(realVoiceChar);
                        }
                        if(article.getSubPic5().getKorChar()!=korChar){
                            article.getSubPic5().setKorChar(korChar);
                        }
                        if(article.getSubPic5().getKorVoiceChar()!=korVoiceChar){
                            article.getSubPic5().setKorVoiceChar(korVoiceChar);
                        }
                        break;
                    default:
                        // 예외 처리나 로깅 등
                        break;
                }
            }
        }
        return article;
    }

    private void saveImage(MultipartFile[] files, String category, Article article) throws IOException {
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
    }

    private void updateImage(MultipartFile[] newFiles, String category, Article article) throws IOException {
        for(int i=0;i<newFiles.length;i++){
            if(!newFiles[i].isEmpty()){
                log.info("기존이미지삭제 : ",article.getMain_pic());
                
                switch(i){
                    case 0 : String beforeFilePath0=uploadDir+category + "/"+article.getMain_pic();
                            File beforeFile0=new File(beforeFilePath0);
                            if(beforeFile0.exists()){
                                beforeFile0.delete();
                            }
                            break;
                    case 1 : String beforeFilePath1=uploadDir+category + "/"+article.getSubPic1().getPic();
                            File beforeFile1=new File(beforeFilePath1);
                            if(beforeFile1.exists()){
                                beforeFile1.delete();
                            }
                            break;
                    case 2 : String beforeFilePath2=uploadDir+category + "/"+article.getSubPic2().getPic();
                            File beforeFile2=new File(beforeFilePath2);
                            if(beforeFile2.exists()){
                                beforeFile2.delete();
                            }
                            break;
                    case 3 : String beforeFilePath3=uploadDir+category + "/"+article.getSubPic3().getPic();
                            File beforeFile3=new File(beforeFilePath3);
                            if(beforeFile3.exists()){
                                beforeFile3.delete();
                            }
                            break;
                    case 4 : String beforeFilePath4=uploadDir+category + "/"+article.getSubPic4().getPic();
                            File beforeFile4=new File(beforeFilePath4);
                            if(beforeFile4.exists()){
                                beforeFile4.delete();
                            }
                            break;
                    case 5 : String beforeFilePath5=uploadDir+category + "/"+article.getSubPic5().getPic();
                            File beforeFile5=new File(beforeFilePath5);
                            if(beforeFile5.exists()){
                                beforeFile5.delete();
                            }
                            break;
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
    }

    private void deleteImage(String category, Article article) {
        for(int i=0;i<6;i++){
            
            log.info("이미지삭제 : {}", i);
            switch(i){
                case 0 :
                    if(article.getMain_pic()!=null){
                        String beforeFilePath0=uploadDir+category + "/"+article.getMain_pic();
                        File beforeFile0=new File(beforeFilePath0);
                        if(beforeFile0.exists()){
                            beforeFile0.delete();
                        }
                    }    
                    break;
                case 1 : 
                    if(!article.getSubPic1().getPic().isEmpty()){
                        String beforeFilePath1=uploadDir+category + "/"+article.getSubPic1().getPic();
                        File beforeFile1=new File(beforeFilePath1);
                        if(beforeFile1.exists()){
                            beforeFile1.delete();
                        }
                    }
                    break;
                case 2 : 
                    if(!article.getSubPic2().getPic().isEmpty()){
                        String beforeFilePath2=uploadDir+category + "/"+article.getSubPic2().getPic();
                        File beforeFile2=new File(beforeFilePath2);
                        if(beforeFile2.exists()){
                            beforeFile2.delete();
                        }
                    }
                    break;
                    
                case 3 : 
                    if(!article.getSubPic3().getPic().isEmpty()){
                        String beforeFilePath3=uploadDir+category + "/"+article.getSubPic3().getPic();
                        File beforeFile3=new File(beforeFilePath3);
                        if(beforeFile3.exists()){
                            beforeFile3.delete();
                        }
                    }
                    break;
                case 4 : 
                    if(!article.getSubPic4().getPic().isEmpty()){
                        String beforeFilePath4=uploadDir+category + "/"+article.getSubPic4().getPic();
                        File beforeFile4=new File(beforeFilePath4);
                        if(beforeFile4.exists()){
                            beforeFile4.delete();
                        }
                    }
                    break;
                case 5 : 
                    if(!article.getSubPic5().getPic().isEmpty()){
                        String beforeFilePath5=uploadDir+category + "/"+article.getSubPic5().getPic();
                        File beforeFile5=new File(beforeFilePath5);
                        if(beforeFile5.exists()){
                            beforeFile5.delete();
                        }
                    }
                    break;
                }   
            }
    }
}
