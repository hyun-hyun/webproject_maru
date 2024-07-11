package com.example.webproject_maru.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webproject_maru.entity.Article;


public interface ArticleRepository extends JpaRepository<Article,Long> {
    @Override
    ArrayList<Article> findAll();//Iterabel -> ArrayList수정

}
