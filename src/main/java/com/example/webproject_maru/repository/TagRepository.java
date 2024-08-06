package com.example.webproject_maru.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webproject_maru.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
    Tag findByTag(String tag);
}
