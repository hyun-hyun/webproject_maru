package com.example.webproject_maru.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.ReviewDto;
import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_r_t;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.entity.Review;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.ArticleRepository;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j //로깅
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private Map_r_tService map_r_tService;
    @Autowired
    private Map_a_tService map_a_tService;
    @Autowired
    private TagService tagService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REVIEW_EXISTS_KEY = "review:exists:"; // Redis 키 prefix
    private static final String RECOMMEND_EXISTS_KEY = "recommend:exists:"; // Redis 키 prefix

    //리뷰 조회(전체)
    public List<ReviewDto> reviews(Long articleId){
        return reviewRepository.findByArticleId(articleId)//리뷰 엔터티 목록 조회
                .stream()//댓글 엔티티 목록을 스트림으로 변환
                .map(review -> ReviewDto.createReviewDto(review))//엔티티를 DTO로 매핑
                .collect(Collectors.toList());//스트림을 리스트 자료형으로 변환
    }
    //리뷰 조회(존재여부만)
    public boolean existsByArticleId(Long articleId){
        return reviewRepository.existsByArticleId(articleId);
    }

    //리뷰 조회(로그인한 사용자)
    public ReviewForm my_review(Long articleId, Long memberId){
        ReviewForm reviewForm = ReviewForm.createReviewForm(reviewRepository.findByArticleMemberId(articleId, memberId));
        if(reviewForm != null){
            List<String> my_selectingTags=map_r_tService.findTagsByReviewId(reviewForm.getId());
            reviewForm.setSelectingTags(my_selectingTags);
        }
        return reviewForm;
    }

    //리뷰 생성
    @Transactional
    public ReviewForm create(ReviewForm dto){
        // 0. Redis에서 중복 확인
        String redisKey = REVIEW_EXISTS_KEY + dto.getMember_id() + ":" + dto.getArticle_id();
        Boolean isReviewExists = redisTemplate.hasKey(redisKey);

        if (Boolean.TRUE.equals(isReviewExists)) {
            throw new IllegalStateException("이미 이 작품에 대해 리뷰하였습니다.");
        }

        // 0.1. DB에서 중복 확인 (예외 처리)
        if (reviewRepository.existsByMember_idAndArticle_id(dto.getMember_id(), dto.getArticle_id())) {
            throw new IllegalStateException("이미 이 작품에 대해 리뷰하였습니다.");
        }

        // 0.3. Redis 추천용 키 삭제
        String redisKey_recommend = RECOMMEND_EXISTS_KEY + dto.getMember_id();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey_recommend))) {
            redisTemplate.delete(redisKey_recommend);
        }


        //1. 게시글 조회 및 예외 발생
        Article article=articleRepository.findById(dto.getArticle_id())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        Member member=memberRepository.findById(dto.getMember_id())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 회원이 없습니다."));//작성자 없으면 에러 메시지 출력
        //2. 리뷰 엔티티 생성
        Review review=Review.createReview(dto, member, article);
        //3. 리뷰 엔티티를 DB에 저장
        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        review.setAppendTime(SeoulNow);
        review.setUpdateTime(SeoulNow);
        Review created=reviewRepository.save(review);
        //리뷰 개수 갱신(article 값)
        Long t_cscore=article.getCscore();
        article.setCscore(t_cscore+1);
        //리뷰 평균 갱신(article 값)
        Double t_avgscore=reviewRepository.getScoreAverage(dto.getArticle_id());
        article.setAvgscore(t_avgscore !=null ? (double)Math.round(t_avgscore*10.0)/10.0 : 0.0);
        articleRepository.save(article);
        ReviewForm resultR=ReviewForm.createReviewForm(created);
        log.info("리뷰저장");
        //selectingTags 저장
        for(String tagName:dto.getSelectingTags()){
            if(!tagName.equals("") && !tagName.equals("on")){
            Tag tag=map_r_tService.findOrCreateTag(article, tagName, resultR.getId());
            log.info(tag.getTag());
            map_r_tService.saveMap_r_t(review, tag);
            }
        }

        //0.2. Redis에 작성 여부 캐싱 (TTL 설정: 2분)
        redisTemplate.opsForValue().set(redisKey, true, Duration.ofMinutes(2));

        //4. DTO로 변환해 반환
        return resultR;
    }

    //리뷰 수정
    @Transactional
    public ReviewForm update(Long id, ReviewForm dto){

        log.info("update 들어옴");
        log.info("Updating review with ID: " + id); // ID 값 확인
        log.info("ReviewForm ID: " + dto.getId()); // DTO의 ID 값 확인
        //1. 리뷰 조회 및 예외 발생
        Review target=reviewRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("리뷰 수정 실패!"+"대상 리뷰가 없습니다."));
        Article article=articleRepository.findById(dto.getArticle_id())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        log.info("update entity가져옴");

        //리뷰의 기존 태그 리스트
        List<String> beforeSelectingTags=map_r_tService.findTagsByReviewId(id);
        log.info("update 기존태그가져옴");

        //2. 리뷰 수정
        //기존태그중에 체크제거된 태그 삭제
        for(String tag:beforeSelectingTags){
            if(!dto.getSelectingTags().contains(tag)){
                map_r_tService.deleteByReviewAndTagName(id, tag);
            }
        }
        //새로 추가된 태그 저장
        for(String tag:dto.getSelectingTags()){
            if(!tag.equals("")&&!tag.equals("on")&&!beforeSelectingTags.contains(tag)){
                Tag newTag=map_r_tService.findOrCreateTag(article, tag);
                map_r_tService.saveMap_r_t(target, newTag);
            }
        }
        //평점과 한줄평 저장
        target.patch(dto);
        //updateTime 갱신
        LocalDateTime SeoulNow=LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        target.setUpdateTime(SeoulNow);
        //리뷰 평균 갱신(article 값)
        Double t_avgscore=reviewRepository.getScoreAverage(dto.getArticle_id());
        article.setAvgscore(t_avgscore !=null ? (double)Math.round(t_avgscore*10.0)/10.0 : 0.0);
        articleRepository.save(article);
        //3. DB로 갱신
        Review updated=reviewRepository.save(target);
        //4. 리뷰 엔티티를 dto로 변환 및 반환
        return ReviewForm.createReviewForm(updated);

    }

    //리뷰 삭제
    @Transactional
    public ReviewForm delete(Long id, Long articleId){
        //1.리뷰 조회 및 예외발생
        Review target=reviewRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("리뷰 삭제 실패!"+"대상이 없습니다."));

        // 0.3. Redis 중복 확인용 키 삭제
        String redisKey = REVIEW_EXISTS_KEY + target.getMember().getId() + ":" + target.getArticle().getId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            redisTemplate.delete(redisKey);
        }

        //2. 리뷰 삭제
        //연관된 Map_r_t삭제
        map_r_tService.deleteByReviewId(id);
        reviewRepository.delete(target);
        //2-2. article t_cscore랑 t_avgscore 갱신
        Article article=articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 삭제 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        Long t_cscore=article.getCscore();
        article.setCscore(t_cscore-1);
        //리뷰 평균 갱신(article 값)
        Double t_avgscore=reviewRepository.getScoreAverage(articleId);
        article.setAvgscore(t_avgscore !=null ? (double)Math.round(t_avgscore*10.0)/10.0 : 0.0);
        articleRepository.save(article);

        //사용자 추가 태그 다른사용자가 미사용시 map_a_t와 tag에서 삭제
        Tag tag=map_a_tService.findTagIdByReviewId(id);
        log.info("삭제시 tag");
        if(tag!=null){
            boolean isTagUsed=map_r_tService.isTagUsed(tag.getId(), articleId);
            log.info("isTagUsed : {}"+isTagUsed);
            if(!isTagUsed){
                map_a_tService.deleteByArticleIdAndTagName(articleId, tag.getTag());
                boolean isTagUsedTotal=map_a_tService.isTagUsedTotal(tag.getId());
                if(!isTagUsedTotal){
                    tagService.deleteById(tag.getId());
                }
            }
        }
        

        //3. 삭제 리뷰를 dto로 변환 및 반환
        return ReviewForm.createReviewForm(target);
    }

    //리뷰삭제(articleId기준으로 한번에)
    @Transactional
    public void deleteByArticleId(Long articleId){
        reviewRepository.deleteByArticleId(articleId);
    }

    //자신이 쓴 리뷰목록
    public Article findArticleByReviewId(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .map(Review::getArticle)  // Review에서 Article을 가져옴
            .orElseThrow(() -> new EntityNotFoundException("reviewId에 따른 게시글 찾지 못함 : " + reviewId));
    }
    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityNotFoundException("reviewId에 따른 리뷰 찾지 못함: " + reviewId));
    }

    //사용자가 작성한 리뷰 전체
    public List<Long> getAllReviewIdByMemberId(Long memberId) {
        return reviewRepository.getAllReviewIdByMemberId(memberId);
    }
    public Long countReviewByMemberId(Long memberId) {
        return reviewRepository.countReviewByMemberId(memberId);
    }

    //추가: 사용자가 리뷰한 작품ID 가져오기
    public List<Long> getReviewedArticleIdsByMemberId(Long memberId) {
        return reviewRepository.findReviewedArticleIdsByMemberId(memberId);
    }

/*
    //articleId에 따른 tag 선택된거 조회
    public List<Map_r_t> getReviewTagsByArticleId(Long articleId){
        return reviewRepository.findTagsByArticleId(articleId);
    }
    //memberId에 따른 tag조회
    public List<Map_r_t> getReviewTagsByMemberId(Long memberId){
        return reviewRepository.findTagsByMemberId(memberId);
    }*/
}
