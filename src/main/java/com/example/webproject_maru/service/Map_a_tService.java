package com.example.webproject_maru.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.Map_a_tRepository;

import jakarta.transaction.Transactional;

@Service
public class Map_a_tService {
    @Autowired
    private Map_a_tRepository map_a_tRepository;
    @Autowired
    private TagService tagService;

    //article만들 때 tag저장
    @Transactional
    public Tag findOrCreateTag(String tagName){
        Tag tag=tagService.findByTag(tagName);
        if(tag==null){
            tag=tagService.saveTag(new Tag(tagName));
        }
        return tag;
    }

    @Transactional
    public void saveMap_a_t(Article article, Tag tag){
        Map_a_t map=new Map_a_t(article, tag);
        map_a_tRepository.save(map);
    }

}
