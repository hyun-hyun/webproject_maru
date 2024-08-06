package com.example.webproject_maru.repository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;


public interface ArticleRepository extends JpaRepository<Article,Long> {
    @Override
    ArrayList<Article> findAll();//Iterabel -> ArrayList수정

    // ID 내림차순으로 모든 Article 가져오기
    ArrayList<Article> findAllByOrderByIdDesc();

    //articleId에 따른 tag
    @Query("SELECT a.tags FROM Article a WHERE a.id=:articleId")
    List <Map_a_t> findTagsByArticleId(@Param("articleId") Long articleId);
}
