package com.example.webproject_maru.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.TagCountForm;
import com.example.webproject_maru.entity.Tag;
import com.example.webproject_maru.repository.Map_r_tRepository;

@Service
public class Map_r_tService {
    @Autowired
    private Map_r_tRepository map_r_tRepository;

    public List<TagCountForm> countTagSelectionsByArticleId(Long articleId) {
    List<Object[]> results = map_r_tRepository.countTagSelectionsByArticleId(articleId);
    return results.stream()
                  .map(
                      result -> new TagCountForm((String) result[0],  // tagName
                      (Long) result[1])   // COUNT
                  ).collect(Collectors.toList());
}
    
}
