package com.example.webproject_maru.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Love;
import com.example.webproject_maru.entity.Member;

public interface LoveRepository extends JpaRepository<Love,Long>{
    Optional<Love> findByMemberAndArticle(Member member, Article article);//게시글에서 찜여부 확인
    boolean existsByMemberIdAndArticleId(Long memberId, Long articleId);

    @Query("SELECT l.article.id FROM Love l WHERE l.member.id = :memberId ORDER BY l.id DESC")
    List<Long> findByMemberId(@Param("memberId") Long memberId); // 특정 회원의 찜 목록 조회

}
