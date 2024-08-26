package com.example.webproject_maru.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.Tag;

public interface Map_a_tRepository extends JpaRepository<Map_a_t, Long>{
    @Query("SELECT m.tag FROM Map_a_t m WHERE m.article.id=:articleId")
    List<Tag> findTagsByArticleId(@Param("articleId") Long articleId);
}
