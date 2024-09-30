package com.example.webproject_maru.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.TagCountForm;
import com.example.webproject_maru.service.Map_r_tService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/mypage")
public class MyPageApiController {
    @Autowired
    private Map_r_tService map_r_tService;

    @GetMapping("/wordcloud/{memberId}")
    public ResponseEntity<List<TagCountForm>> getWordCloudData(@PathVariable Long memberId) {
        log.info("wordcloud api");
        List<TagCountForm> tags = map_r_tService.countTagSelectionsByMemberId(memberId);
        return ResponseEntity.ok(tags);
    }
    
}
