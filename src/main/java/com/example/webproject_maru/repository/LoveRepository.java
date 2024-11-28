package com.example.webproject_maru.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Love;
import com.example.webproject_maru.entity.Member;

public interface LoveRepository extends JpaRepository<Love,Long>{
    Optional<Love> findByMemberAndArticle(Member member, Article article);
    List<Love> findByMemberId(Long memberId); // 특정 회원의 찜 목록 조회
    boolean existsByMemberIdAndArticleId(Long memberId, Long articleId);
}
