package com.example.webproject_maru.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webproject_maru.entity.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Override
    ArrayList<Review> findAll();//Iterabel -> ArrayList수정
}