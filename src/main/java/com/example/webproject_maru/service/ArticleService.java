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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.ArticleListDto;
import com.example.webproject_maru.dto.SubPicForm;
import com.example.webproject_maru.dto.TagCountDto;
import com.example.webproject_maru.dto.TagDto;
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
    private TagService tagService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CommentService commentService;


    //@Value("${file.upload-dir}")
    private String uploadDir="/app/images/pic/";

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


    //검색
    public Page<Article> searchArticles(String query, Pageable pageable) {
        return articleRepository.searchByQuery(query, pageable);
    }
    
    public ArrayList<Article> findArticles(){
        ArrayList<Article> articleEntityList=articleRepository.findAll();
        return articleEntityList;
    }
    /* 
    public Article findByIdArticle(Long id){
        Article articleEntity=articleRepository.findById(id).orElse(null);
        return articleEntity;
    }
    */
    public Optional<Article> findById(Long id){
        return articleRepository.findById(id);
    }

/* 
    //작품등록 최신순 정렬
    public ArrayList<Article> findArticlesDesc(){
        ArrayList<Article> articleEntityList=articleRepository.findAllByOrderByIdDesc();
        return articleEntityList;
    }
    //작품등록 최신순 정렬(페이징)
    public Page<Article> findArticlesDesc(Pageable pageable){
        return articleRepository.findAllByOrderByIdDesc(pageable);
    }
*/
    //작품 정렬(페이징)
    public Page<Article> findArticles(Pageable pageable){
        return articleRepository.findAll(pageable);
    }


    //작품등록 최신순 정렬(15개 페이징)
    public List<Article> findLimitArticlesDesc(int limit){
        Pageable pageable = PageRequest.of(0, limit); // 페이지 번호 0, 개수 limit
        List<Article> articleEntityList=articleRepository.findLimitByOrderByIdDesc(pageable).getContent();
        return articleEntityList;
    }

    //3개월간 점수 높은순 정렬(50개 페이징)
    public List<Article> get3mRecentHighScoreArticles(int limit) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        Pageable pageable = PageRequest.of(0, limit); // 페이지 번호 0, 개수 limit
        return articleRepository.findRecentHighScoreArticles(threeMonthsAgo, pageable).getContent();
    }

    //1개월간 점수 높은순 정렬(15개 페이징)
    public List<Article> getRecentHighScoreArticles(int limit) {
        LocalDateTime oneMonthsAgo = LocalDateTime.now().minusMonths(1);
        Pageable pageable = PageRequest.of(0, limit); // 페이지 번호 0, 개수 limit
        return articleRepository.findRecentHighScoreArticles(oneMonthsAgo, pageable).getContent();
    }
    
    //articleId별 tag조회
    public List<String> getTagsByArticleId(Long articleId){
        return map_a_tService.getTagsByArticleId(articleId);
    }
/* 
    public List<TagCountDto> countTagSelectionsByArticleId(Long articleId){
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
            List<String> usedTags=map_r_tService.getAllTagsByArticleId(articleId);
            if(usedTags!=null){
                articleEntity.setUsedTags(usedTags);
            }
        }
        return articleEntity;
    }
*/
    //수정데이터 반영
    @Transactional
    public Article update(ArticleForm form, MultipartFile[] newFiles, SubPicForm[] subPicForms, String category){
        //1. Article 조회
        Article article=articleRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 수정 실패!"+
                        "대상 게시글이 없습니다."));//해당게시글 없으면 에러 메시지 출력
        Member member=memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 수정 실패!"+
                        "작성자 memberId가 없습니다."));//해당멤버 없으면 에러 메시지 출력
        //기존태그 리스트
        List<String> beforeTags=getTagsByArticleId(form.getId());
        //2. Article 수정
        //기본내용
        article.patch(form, member);
        Article updating=updateSubPics(subPicForms, article);
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
    public void delete(ArticleForm form, String category, boolean existingReview, boolean existingComment){
        //삭제대상 가져오기
        Article article=findById(form.getId()).orElseThrow(() -> new IllegalArgumentException("게시글 삭제 실패!"+
        "대상 게시글이 없습니다."));;

        if(article!=null){
            Long articleId=article.getId();
            //태그삭제(map_a_t, tag)
            deleteMap_a_tAndTagByArticleId(form.getId());
            //리뷰삭제
            if(existingReview){
                reviewService.deleteByArticleId(articleId);
            }
            //댓글삭제
            if(existingComment){
                commentService.deleteByArticleId(articleId);
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
        List<SubPic> subPics = new ArrayList<>();
    
        for (int i = 0; i < subPicForms.length; i++) {
            SubPicForm subPicForm = subPicForms[i];
            if (subPicForm != null) {
                SubPic subPic = subPicForm.toEntity();
                // Name과 Pic 값 설정
                if (i > 0) { // 첫 번째 서브픽은 메인픽에서 가져옴
                    subPic.setName(subPicForm.getRealChar()); // 예시로 실사 캐릭터명을 name으로 사용
                    subPic.setPic(""); // 나중에 파일 저장 후 설정
                }
                subPics.add(subPic);
            }
        }
        article.setSubPics(subPics);
    }
    
    private Article updateSubPics(SubPicForm[] subPicForms, Article article) {
        List<SubPic> subPics = article.getSubPics();
    
        for (int i = 0; i < subPicForms.length; i++) {
            SubPicForm subPicForm = subPicForms[i];
    
            if (subPicForm != null && i < subPics.size()) {
                updateSubPic(subPics.get(i), subPicForm);
            }
        }
    
        return article;
    }
    
    private void updateSubPic(SubPic subPic, SubPicForm subPicForm) {
        if (subPic.getRealChar() != null && !subPic.getRealChar().equals(subPicForm.getRealChar())) {
            subPic.setRealChar(subPicForm.getRealChar());
        }
        if (subPic.getRealVoiceChar() != null && !subPic.getRealVoiceChar().equals(subPicForm.getRealVoiceChar())) {
            subPic.setRealVoiceChar(subPicForm.getRealVoiceChar());
        }
        if (subPic.getKorChar() != null && !subPic.getKorChar().equals(subPicForm.getKorChar())) {
            subPic.setKorChar(subPicForm.getKorChar());
        }
        if (subPic.getKorVoiceChar() != null && !subPic.getKorVoiceChar().equals(subPicForm.getKorVoiceChar())) {
            subPic.setKorVoiceChar(subPicForm.getKorVoiceChar());
        }
    }
    

    private void saveImage(MultipartFile[] files, String category, Article article) throws IOException {
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isEmpty()) {
                log.info("저장시작");
                UUID uuid = UUID.randomUUID();
                String ori_name = files[i].getOriginalFilename();
                String extension = ori_name.substring(ori_name.lastIndexOf("."));
                String saveFileName = uuid.toString() + extension;
                Path filePath = Paths.get(uploadDir + category + "/" + saveFileName);
    
                Files.createDirectories(filePath.getParent());
                files[i].transferTo(filePath);
    
                log.info(uploadDir + category + "/" + saveFileName);
                log.info("i=" + i);
    
                if (i == 0) {
                    article.setMain_pic_name(files[0].getOriginalFilename());
                    article.setMain_pic(saveFileName);
                } else {
                    SubPic subPic = article.getSubPics().get(i - 1); // 서브픽은 인덱스가 1부터 시작
                    subPic.setName(files[i].getOriginalFilename());
                    subPic.setPic(saveFileName);
                }
            }
        }
    }

    private void updateImage(MultipartFile[] newFiles, String category, Article article) throws IOException {
        for (int i = 0; i < newFiles.length; i++) {
            if (!newFiles[i].isEmpty()) {
                log.info("기존이미지삭제 : ", article.getMain_pic());
    
                if (i == 0) {
                    String beforeFilePath0 = uploadDir + category + "/" + article.getMain_pic();
                    deleteFile(beforeFilePath0);
                } else {
                    if (i - 1 < article.getSubPics().size()) {
                        String beforeFilePath = uploadDir + category + "/" + article.getSubPics().get(i - 1).getPic();
                        deleteFile(beforeFilePath);
                    }
                }
    
                // 새로운 이미지 저장
                UUID uuid = UUID.randomUUID();
                String oriName = newFiles[i].getOriginalFilename();
                String extension = oriName.substring(oriName.lastIndexOf("."));
                String saveFileName = uuid.toString() + extension;
                Path filePath = Paths.get(uploadDir + category + "/" + saveFileName);
                log.info("신규 이미지 저장 : ", saveFileName);
                Files.createDirectories(filePath.getParent());
                newFiles[i].transferTo(filePath);
    
                if (i == 0) {
                    article.setMain_pic_name(newFiles[0].getOriginalFilename());
                    article.setMain_pic(saveFileName);
                } else {
                    if (i - 1 < article.getSubPics().size()) {
                        SubPic subPic = article.getSubPics().get(i - 1);
                        subPic.setName(newFiles[i].getOriginalFilename());
                        subPic.setPic(saveFileName);
                    }
                }
            }
        }
    }
    
    private void deleteImage(String category, Article article) {
        // 메인이미지 삭제
        if (article.getMain_pic() != null) {
            String beforeFilePath0 = uploadDir + category + "/" + article.getMain_pic();
            deleteFile(beforeFilePath0);
        }
    
        // 등장인물 이미지 삭제
        for (SubPic subPic : article.getSubPics()) {
            if (subPic.getPic() != null && !subPic.getPic().isEmpty()) {
                String beforeFilePath = uploadDir + category + "/" + subPic.getPic();
                deleteFile(beforeFilePath);
            }
        }
    }
    
    // 이미지 삭제
    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }


    
    
}
