package com.example.webproject_maru.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.service.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagApiController {
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public List<Tag> getAllTags(){
        return tagService.getAllTags();
    }

    @PostMapping
    public Tag createTag(@RequestBody Tag tag){
        return tagService.saveTag(tag);
    }
    
}
