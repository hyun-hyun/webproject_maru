package com.example.webproject_maru.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.TagRepository;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    //전체 tag조회
    public List<Tag> getAllTags(){
        return tagRepository.findAll();
    }
    
    //id별 tag조회
    public Tag getTagById(Long id){
        return tagRepository.findById(id).orElse(null);
    }

    //tag저장
    public Tag saveTag(Tag tag){
        return tagRepository.save(tag);
    }

    //기존tag에 있는지 tag찾기
    public Tag findByTag(String tagName){
        return tagRepository.findByTag(tagName);
    }
}
