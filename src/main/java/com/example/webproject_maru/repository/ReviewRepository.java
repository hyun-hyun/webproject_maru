package com.example.webproject_maru.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Map_r_t;
import com.example.webproject_maru.entity.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Override
    ArrayList<Review> findAll();//Iterabel -> ArrayList수정
    Double getScoreAverage(Long articleId);

    //redis 중복확인위해
    boolean existsByMember_idAndArticle_id(Long member_id, Long article_id);

    //특정 게시글의 모든 리뷰 조회
    @Query(value=
                "SELECT * "+
                "FROM review "+
                "WHERE article_id= :articleId",
            nativeQuery=true)//SQL문으로 작성
    List<Review> findByArticleId(Long articleId);

    //특정게시글의 리뷰 존재여부 조회
    @Query("SELECT COUNT(r) >0 FROM Review r WHERE r.article.id = :articleId")
    boolean existsByArticleId(@Param("articleId") Long articleId);

    //특정 회원의 특정게시글 리뷰 조회
    @Query(value=
                "SELECT * "+
                "FROM review "+
                "WHERE article_id= :articleId "+
		"AND member_id= :memberId",
            nativeQuery=true)//SQL문으로 작성
    Review findByArticleMemberId(Long articleId, Long memberId);

    //articleId로 리뷰 삭제
    void deleteByArticleId(Long articleId);

    //사용자가 작성한 리뷰 전체
    @Query("SELECT r.id FROM Review r WHERE r.member.id=:memberId ORDER BY r.id DESC")
    List<Long> getAllReviewIdByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT count(r.id) FROM Review r WHERE r.member.id=:memberId")
    Long countReviewByMemberId(@Param("memberId") Long memberId);
    
    //추가: 사용자가 리뷰한 작품ID 가져오기
    @Query("SELECT r.article.id FROM Review r WHERE r.member.id = :memberId")
    List<Long> findReviewedArticleIdsByMemberId(@Param("memberId") Long memberId);
    
    /*@Query("SELECT r.selectedTags FROM Review r WHERE r.article.id =:articleId")
    List<Map_r_t> findTagsByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT r.selectedTags FROM Review r WHERE r.member.id=:memberId")
    List<Map_r_t> findTagsByMemberId(@Param("memberId") Long memberId);*/
}