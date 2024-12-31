package com.example.webproject_maru.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.TagDto;
import com.example.webproject_maru.dto.TagCountDto;
import com.example.webproject_maru.repository.ArticleRepository;
import com.example.webproject_maru.repository.ReviewRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecommendationService {
    @Autowired
    private Map_r_tService map_r_tService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private Map_a_tService map_a_tService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RECOMMEND_EXISTS_KEY = "recommend:exists:"; // Redis 키 prefix

    public List<Long> recommendArticleIds(Long memberId) {
        // 0. Redis에서 중복 확인
        String redisKey = RECOMMEND_EXISTS_KEY + memberId;
        Boolean isRecommendExists = redisTemplate.hasKey(redisKey);
        // 0.1. Redis에 있을 경우 해당 추천 게시글 ID 사용
        if (Boolean.TRUE.equals(isRecommendExists)) {
            // Redis에서 가져온 값을 List<Long>로 캐스팅
            Object cachedRecommendation = redisTemplate.opsForValue().get(redisKey);
            if (cachedRecommendation instanceof List<?>) {
                log.info("*********** 캐싱활용**********");
                // 리스트가 List<Long>인 경우만 캐스팅하여 반환
                return (List<Long>) cachedRecommendation;
            } else {
                log.error("Redis에서 추천 게시글 ID를 가져오는 데 실패했습니다. 캐시된 데이터 형식이 잘못되었습니다.");
                return new ArrayList<>();
            }
        }
        log.info("!!!!!! 캐싱no!!!!!!!!!!!");

        // 1단계: 멤버의 태그 데이터 가져오기
        List<TagDto> memberTags = map_r_tService.getTagSelectionsByMemberId(memberId);
        
        // 2단계: 태그 점수 매핑 (1위: 10점, 2위: 9점 ...)
        Map<Long, Integer> tagScores = new HashMap<>();
        for (int i = 0; i < memberTags.size(); i++) {
            long tagId = memberTags.get(i).getT_id();
            if(i>=10){
                tagScores.put(tagId,1);
            }
            else{
                tagScores.put(tagId, 10 - i); // 예: 10, 9, 8, ...
            }
        }
        //추가: 사용자가 리뷰한 작품ID 가져오기
        List<Long> reviewedArticleIds = reviewService.getReviewedArticleIdsByMemberId(memberId);

        // 3단계: 각 게시글별 리뷰에서 선택된 태그의 종류와 개수 찾기
        Map<Long, Long> articleScores = new HashMap<>(); // 게시글 ID와 총 점수를 저장

        for (TagDto tag : memberTags) {
            List<Long> articleIds = map_a_tService.findArticleIdsByTagId(tag.getT_id());

            for (Long articleId : articleIds) {
                if (reviewedArticleIds.contains(articleId)) {
                    continue; // 사용자가 이미 리뷰한 작품은 추천에서 제외
                }

                long tagCount = map_r_tService.countTagByArticleIdAndTagId(tag.getT_id(), articleId);//해당 게시글의 태그 개수
                int score = tagScores.get(tag.getT_id());

                articleScores.merge(articleId, tagCount * score, Long::sum);
            }
        }

        // 4단계: 게시글 점수 정렬
        List<Map.Entry<Long, Long>> sortedArticles = new ArrayList<>(articleScores.entrySet());
        sortedArticles.sort((a, b) -> Long.compare(b.getValue(), a.getValue())); // 내림차순 정렬

        //5단계: Article ID 반환
        List<Long> recommendedArticleIds = sortedArticles.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("추천 작품 ID: {}", recommendedArticleIds);

        //0.2. Redis에 작성 여부 캐싱 (TTL 설정: 36시간)
        redisTemplate.opsForValue().set(redisKey, recommendedArticleIds, Duration.ofHours(36));
        return recommendedArticleIds;
    }
    
}
