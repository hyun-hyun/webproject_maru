package com.example.webproject_maru.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Long> recommendArticleIds(Long memberId) {
        log.info("추천 서비스 진입");
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

        // 3단계: 각 게시글별 리뷰에서 선택된 태그의 종류와 개수 찾기
        Map<Long, Long> articleScores = new HashMap<>(); // 게시글 ID와 총 점수를 저장

        for (TagDto tag : memberTags) {
            List<Long> articleIds = map_a_tService.findArticleIdsByTagId(tag.getT_id());

            for (Long articleId : articleIds) {
                long tagCount = map_r_tService.countTagByArticleIdAndTagId(tag.getT_id(), articleId); // 해당 게시글의 태그 개수
                int score = tagScores.get(tag.getT_id());
                
                articleScores.merge(articleId, tagCount * score, Long::sum);
            }
        }

        // 4단계: 게시글 점수 정렬
        List<Map.Entry<Long, Long>> sortedArticles = new ArrayList<>(articleScores.entrySet());
        sortedArticles.sort((a, b) -> Long.compare(b.getValue(), a.getValue())); // 내림차순 정렬

        // 상위 8개 Article ID 반환
        List<Long> recommendedArticleIds = new ArrayList<>();
        for (int i = 0; i < Math.min(8, sortedArticles.size()); i++) {
            recommendedArticleIds.add(sortedArticles.get(i).getKey());
        log.info("추천하는 첫번째 작품 id: {}",sortedArticles.get(i).getKey());

        }

        return recommendedArticleIds;
    }
}
