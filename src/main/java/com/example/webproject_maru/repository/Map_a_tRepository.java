package com.example.webproject_maru.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.dto.TagForm;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.Tag;

public interface Map_a_tRepository extends JpaRepository<Map_a_t, Long>{
    /* 
    @Query("SELECT m.tag.id, m.tag.tag FROM Map_a_t m WHERE m.article.id=:articleId")
    List<Object[]> findTagsByArticleId(@Param("articleId") Long articleId);
*/
    // articleId로 태그 목록을 가져오는 쿼리
    @Query("SELECT m.tag.tag FROM Map_a_t m WHERE m.article.id = :articleId" )
    List<String> getTagsByArticleId(@Param("articleId") Long articleId);

    //articleId와 태그명으로 Map_a_t삭제
    @Modifying
    @Query("DELETE FROM Map_a_t m WHERE m.article.id=:articleId AND m.tag.tag=:tagName")
    void deleteByArticleIdAndTagName(@Param("articleId") Long articleId, @Param("tagName") String tagName);

    //articleId로 Map_a_t삭제
    @Modifying
    @Query("DELETE FROM Map_a_t m WHERE m.article.id=:articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);
}
