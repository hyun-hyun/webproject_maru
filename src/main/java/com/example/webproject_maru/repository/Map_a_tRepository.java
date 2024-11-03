package com.example.webproject_maru.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.dto.TagDto;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.Tag;

public interface Map_a_tRepository extends JpaRepository<Map_a_t, Long>{
    /* 
    @Query("SELECT m.tag.id, m.tag.tag FROM Map_a_t m WHERE m.article.id=:articleId")
    List<Object[]> findTagsByArticleId(@Param("articleId") Long articleId);
*/
    // articleId로 태그 목록을 가져오는 쿼리
    @Query("SELECT m.tag.tag FROM Map_a_t m LEFT JOIN Map_r_t r ON m.tag.tag = r.tag.tag AND r.review.article.id = :articleId WHERE m.article.id = :articleId GROUP BY m.tag.tag ORDER BY COUNT(r.tag) DESC" )
    List<String> getTagsByArticleId(@Param("articleId") Long articleId);

    //해당게시글에 동일한 태그를 가지고있는지 확인
    @Query("SELECT m.tag FROM Map_a_t m WHERE m.tag.id=:tagId AND m.article.id=:articleId")
    Tag findByTagIdAndArticleId(@Param("tagId") Long tagId, @Param("articleId") Long articleId);

    //articleId와 태그명으로 Map_a_t삭제
    @Modifying
    @Query("DELETE FROM Map_a_t m WHERE m.article.id=:articleId AND m.tag.tag=:tagName")
    void deleteByArticleIdAndTagName(@Param("articleId") Long articleId, @Param("tagName") String tagName);

    //articleId로 Map_a_t삭제
    @Modifying
    @Query("DELETE FROM Map_a_t m WHERE m.article.id=:articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);

    //tagId와 연결된 map_a_t수량파악
    @Query("SELECT COUNT(m) FROM Map_a_t m WHERE m.tag.id=:tagId")
    Long countByTagId(@Param("tagId") Long tagId);
    //추천용
    @Query("SELECT m.article.id FROM Map_a_t m WHERE m.tag.id=:tagId")
    List<Long> findArticleIdsByTagId(@Param("tagId") Long tagId);

    //삭제하려는 리뷰id와 일치하는 tag_id 반환(사용자가 추가한 태그)
    @Query("SELECT m.tag FROM Map_a_t m WHERE m.reviewId=:reviewId")
    Tag findTagIdsByReviewId(@Param("reviewId") Long reviewId);

  
}
