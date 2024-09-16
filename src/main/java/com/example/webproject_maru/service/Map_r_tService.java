package com.example.webproject_maru.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.TagCountForm;
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

    public List<TagCountForm> countTagSelectionsByArticleId(Long articleId) {
    List<Object[]> results = map_r_tRepository.countTagSelectionsByArticleId(articleId);
    return results.stream()
                  .map(
                      result -> new TagCountForm((String) result[0],  // tagName
                      (Long) result[1])   // COUNT
                  ).collect(Collectors.toList());
    }

    //article만들 때 tag저장
    @Transactional
    public Tag findOrCreateTag(String tagName){
        Tag tag=tagService.findByTag(tagName);
        if(tag==null){
            tag=tagService.saveTag(new Tag(tagName));
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
