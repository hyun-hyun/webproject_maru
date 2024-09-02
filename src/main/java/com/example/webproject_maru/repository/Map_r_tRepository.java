package com.example.webproject_maru.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Map_r_t;
import com.example.webproject_maru.entity.Tag;

public interface Map_r_tRepository extends JpaRepository<Map_r_t, Long>{
    @Query("SELECT m.tag.tag, COUNT(m.tag) FROM Map_r_t m WHERE m.review.article.id=:articleId GROUP BY m.tag.tag ORDER BY COUNT(m.tag) DESC" )
    List<Object[]> countTagSelectionsByArticleId(@Param("articleId") Long articleId);
}
