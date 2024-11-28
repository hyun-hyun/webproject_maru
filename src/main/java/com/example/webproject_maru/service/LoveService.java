package com.example.webproject_maru.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Love;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.repository.ArticleRepository;
import com.example.webproject_maru.repository.LoveRepository;
import com.example.webproject_maru.repository.MemberRepository;

@Service
public class LoveService {
    @Autowired
    private LoveRepository loveRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired MemberRepository memberRepository;
    
    // 게시글에 찜한 상태인지 확인하는 메서드
    public boolean isLoved(Long memberId, Long articleId) {
        return loveRepository.existsByMemberIdAndArticleId(memberId, articleId);
    }

    public boolean toggleLove(Long memberId, Long articleId) {
        // Member와 Article 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다."));

        // Love 조회
        Optional<Love> existingLove = loveRepository.findByMemberAndArticle(member, article);

        if (existingLove.isPresent()) {
            // 이미 찜한 경우, 삭제
            loveRepository.delete(existingLove.get());
            return false; // 찜 취소됨
        } else {
            // 찜하지 않은 경우, 추가
            Love love = new Love(member, article);
            loveRepository.save(love);
            return true; // 찜 추가됨
        }
    }

    //마이페이지용 회원별 리뷰한 게시글id
    public List<Long> findArticleIdByMemberId(Long memberId) {
        return loveRepository.findByMemberId(memberId);
    }

}
