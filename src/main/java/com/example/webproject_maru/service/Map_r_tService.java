package com.example.webproject_maru.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.TagCountForm;
import com.example.webproject_maru.dto.TagForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_r_t;
import com.example.webproject_maru.entity.Review;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.Map_r_tRepository;

import jakarta.transaction.Transactional;

@Service
public class Map_r_tService {
    @Autowired
    private Map_r_tRepository map_r_tRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    Map_a_tService map_a_tService;

    //게시글용 태그명, 태그수
    public List<TagCountForm> countTagSelectionsByArticleId(Long articleId) {
    List<Object[]> results = map_r_tRepository.countTagSelectionsByArticleId(articleId);
    return results.stream()
                  .map(
                      result -> new TagCountForm((String) result[0],  // tagName
                      (Long) result[1])   // COUNT
                  ).collect(Collectors.toList());
    }

    //마이페이지 워드클라우드용 태그명, 태그수 _100개상한
    public List<TagCountForm> countTagSelectionsByMemberId(Long memberId){
        List<Object[]> results=map_r_tRepository.countTagSelectionsByMemberId(memberId);
        return results.stream()
                    .map(
                        result -> new TagCountForm((String) result[0],
                                (Long) result[1])
                    ).limit(100).collect(Collectors.toList());
    }

    //마이페이지 추천용 태그명, 태그수 _20개상한
    public List<TagForm> getTagSelectionsByMemberId(Long memberId){
        List<Object[]> results=map_r_tRepository.getTagSelectionsByMemberId(memberId);
        return results.stream()
                    .map(
                        result -> new TagForm((Long) result[0],
                                (String) result[1])
                    ).limit(20).collect(Collectors.toList());
    }

    //마이페이지 추천용 각 작품의 태그별 선택 수
    public Long countTagByArticleIdAndTagId(Long tagId, Long articleId){
        return map_r_tRepository.countTagByArticleIdAndTagId(tagId,articleId);
    }

    //article만들 때 tag저장
    @Transactional
    public Tag findOrCreateTag(Article article, String tagName){
        Tag tag=tagService.findByTag(tagName);
        if(tag==null){
            tag=tagService.saveTag(new Tag(tagName));
            map_a_tService.saveMap_a_t(article, tag);
        }
        return tag;
    }

    public void saveMap_r_t(Review review, Tag tag) {
        Map_r_t map=new Map_r_t(review, tag);
        map_r_tRepository.save(map);
    }
    
    public List<String> findTagsByReviewId(Long reviewId){
        return map_r_tRepository.findTagsByReviewId(reviewId);
    }

    // articleId를 이용하여 태그 목록을 가져오는 메서드(메인화면용)_5개상한
    public List<String> getOnlyTagsByArticleId(Long articleId) {
        List<String> tags =map_r_tRepository.findOnlyTagsByArticleId(articleId);
        // 상위 5개만 반환
        return tags.stream().limit(5).collect(Collectors.toList());

    }

    // articleId를 이용하여 태그 목록을 가져오는 메서드(게시글 수정용)
    public List<String> getOnlyAllTagsByArticleId(Long articleId) {
        List<String> tags =map_r_tRepository.findOnlyTagsByArticleId(articleId);
        return tags.stream().collect(Collectors.toList());

    }


    //리뷰삭제시 연계삭제
    @Transactional
    public void deleteByReviewId(Long reviewId){
        map_r_tRepository.deleteByReviewId(reviewId);
    }

    //리뷰와 태그명을 통해 Map_r_t삭제
    @Transactional
    public void deleteByReviewAndTagName(Long reviewId, String tagName){
        map_r_tRepository.deleteByReviewIdAndTagName(reviewId, tagName);
    }
}
