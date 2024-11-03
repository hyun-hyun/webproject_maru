package com.example.webproject_maru.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.TagDto;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.Map_a_tRepository;

import jakarta.transaction.Transactional;

@Service
public class Map_a_tService {
    @Autowired
    private Map_a_tRepository map_a_tRepository;
    @Autowired
    private TagService tagService;

    //article만들 때 tag저장
    @Transactional
    public Tag findOrCreateTag(String tagName){
        Tag tag=tagService.findByTag(tagName);
        if(tag==null){
            tag=tagService.saveTag(new Tag(tagName));
        }
        return tag;
    }

    //생성
    @Transactional
    public void saveMap_a_t(Article article, Tag tag){
        Map_a_t map=new Map_a_t(article, tag);
        map_a_tRepository.save(map);
    }
    @Transactional//리뷰생성시
    public void saveMap_a_t(Article article, Tag tag, Long reviewId){
        Map_a_t map=new Map_a_t(article, tag, reviewId);
        map_a_tRepository.save(map);
    }

    //tag찾기
    public List<String> getTagsByArticleId(Long articleId){
        return map_a_tRepository.getTagsByArticleId(articleId);
    }

    //article과 tagId로 이 게시글에 해당 태그 있는지 확인
    public Tag findByTagIdAndArticleId(Long tagId, Long articleId){
        return map_a_tRepository.findByTagIdAndArticleId(tagId, articleId);
    }

    //article과 tag명을 통해 Map_a_t삭제
    @Transactional
    public void deleteByArticleIdAndTagName(Long articleId, String tagName){
        map_a_tRepository.deleteByArticleIdAndTagName(articleId, tagName);
    }

    //article삭제시 연계삭제
    @Transactional
    public void deleteByArticleId(Long articleId){
        map_a_tRepository.deleteByArticleId(articleId);
    }

    //tagId와 연결된 map_a_t 수량 파악
    @Transactional
    public Long countByTagId(Long tagId){
        return map_a_tRepository.countByTagId(tagId);
    }
    //추천용
    public List<Long> findArticleIdsByTagId(Long tagId){
        return map_a_tRepository.findArticleIdsByTagId(tagId);
    } 
/* 
    public List<TagForm> findTagsByArticleId(Long articleId) {
        List<Object[]> results = map_a_tRepository.findTagsByArticleId(articleId);
            return results.stream()
                  .map(
                      result -> new TagForm((Long) result[0],  // tag_id
                      (String) result[1])   // tag_name
                  ).collect(Collectors.toList());
    }
*/
    //삭제하려는 리뷰id와 일치하는 tag 반환(사용자가 추가한 태그)
    public Tag findTagIdByReviewId(Long reviewId) {
        return map_a_tRepository.findTagIdsByReviewId(reviewId);
    }

    //사용자가 추가한 태그가 전체적으로 사용중인지 확인
    public boolean isTagUsedTotal(Long tagId) {
        return map_a_tRepository.existsByTagId(tagId);
    }
}
