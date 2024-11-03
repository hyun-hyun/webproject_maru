package com.example.webproject_maru.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Map_r_t;
import com.example.webproject_maru.entity.Tag;

public interface Map_r_tRepository extends JpaRepository<Map_r_t, Long>{
    //게시글용 태그명, 태그수
    @Query("SELECT m.tag.tag, COUNT(m.tag) FROM Map_r_t m WHERE m.review.article.id=:articleId GROUP BY m.tag.tag ORDER BY COUNT(m.tag) DESC" )
    List<Object[]> countTagSelectionsByArticleId(@Param("articleId") Long articleId);

    //마이페이지 작품 추천용 태그id, 태그명
    @Query("SELECT m.tag.id, m.tag.tag FROM Map_r_t m WHERE m.review.member.id=:memberId GROUP BY m.tag.id, m.tag.tag ORDER BY COUNT(m.tag) DESC" )
    List<Object[]> getTagSelectionsByMemberId(@Param("memberId") Long memberId);

    //마이페이지 작품 추천용 각 작품의 태그별 선택된 수
    @Query("SELECT COUNT(m.tag) FROM Map_r_t m WHERE m.tag.id=:tagId AND m.review.article.id=:articleId")
    Long countTagByArticleIdAndTagId(@Param("tagId") Long tagId, @Param("articleId") Long articleId);

    //마이페이지 워드클라우드용 태그명, 태그수
    @Query("SELECT m.tag.tag, COUNT(m.tag) FROM Map_r_t m WHERE m.review.member.id=:memberId GROUP BY m.tag.tag ORDER BY COUNT(m.tag) DESC")
    List<Object[]> countTagSelectionsByMemberId(@Param("memberId") Long memberId);

    //해당 게시글에서 선택된 태그들
    @Query("SELECT m.tag.tag FROM Map_r_t m WHERE m.review.article.id = :articleId GROUP BY m.tag.tag ORDER BY COUNT(m.tag) DESC")
    List<String> findAllTagsByArticleId(@Param("articleId") Long articleId);

    //해당 게시글에서 선택된 태그 5개만
    @Query("SELECT m.tag.tag FROM Map_r_t m WHERE m.review.article.id = :articleId GROUP BY m.tag.tag ORDER BY COUNT(m.tag) DESC")
    Page<String> findOnlyTagsByArticleId(@Param("articleId") Long articleId, Pageable pageable);

    //리뷰시 내가 선택한 태그들
    @Query("SELECT t.tag FROM Map_r_t m JOIN m.tag t WHERE m.review.id = :reviewId")
    List<String> findTagsByReviewId(@Param("reviewId") Long reviewId);

    //리뷰id와 태그명으로 Map_r_t삭제
    @Modifying
    @Query("DELETE FROM Map_r_t m WHERE m.review.id=:reviewId AND m.tag.tag=:tagName")
    void deleteByReviewIdAndTagName(@Param("reviewId") Long reviewId, @Param("tagName") String tagName);

    //리뷰id로 Map_r_t삭제
    @Modifying
    @Query("DELETE FROM Map_r_t m WHERE m.review.id=:reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    //사용자가 추가한 태그가 사용중인지 확인
    @Query("SELECT COUNT(m) > 0 FROM Map_r_t m WHERE m.tag.id = :tagId AND m.review.article.id = :articleId")
    boolean existsByTagIdAndArticleId(@Param("tagId") Long tagId, @Param("articleId") Long articleId);


    
}
