package com.example.webproject_maru.repository;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webproject_maru.entity.Article;


public interface ArticleRepository extends JpaRepository<Article,Long> {
    @Override
    ArrayList<Article> findAll();//Iterabel -> ArrayList수정

    // ID 내림차순으로 모든 Article 가져오기
    ArrayList<Article> findAllByOrderByIdDesc();

}
