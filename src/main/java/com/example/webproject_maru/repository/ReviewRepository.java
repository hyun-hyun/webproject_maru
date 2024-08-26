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

    //특정 게시글의 모든 리뷰 조회
    @Query(value=
                "SELECT * "+
                "FROM review "+
                "WHERE article_id= :articleId",
            nativeQuery=true)//SQL문으로 작성
    List<Review> findByArticleId(Long articleId);

    //특정 회원의 특정게시글 리뷰 조회
    @Query(value=
                "SELECT * "+
                "FROM review "+
                "WHERE article_id= :articleId "+
		"AND member_id= :memberId",
            nativeQuery=true)//SQL문으로 작성
    Review findByArticleMemberId(Long articleId, Long memberId);

    /*@Query("SELECT r.selectedTags FROM Review r WHERE r.article.id =:articleId")
    List<Map_r_t> findTagsByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT r.selectedTags FROM Review r WHERE r.member.id=:memberId")
    List<Map_r_t> findTagsByMemberId(@Param("memberId") Long memberId);*/
}