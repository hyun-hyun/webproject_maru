package com.example.webproject_maru.repository;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.dto.TagDto;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_a_t;


public interface ArticleRepository extends JpaRepository<Article,Long> {
    @Override
    ArrayList<Article> findAll();//Iterabel -> ArrayList수정

    // ID 내림차순으로 모든 Article 가져오기
    ArrayList<Article> findAllByOrderByIdDesc();

    // ID 내림차순으로 모든 Article 가져오기(페이징)
    Page<Article> findAllByOrderByIdDesc(Pageable pageable);

    // ID 내림차순으로 15개 Article 가져오기
    @Query("SELECT a FROM Article a ORDER BY a.id DESC")
    Page<Article> findLimitByOrderByIdDesc(Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.reviews r WHERE r.appendTime >= :threeMonthsAgo GROUP BY a.id ORDER BY AVG(r.score) DESC")
    Page<Article> findRecentHighScoreArticles(@Param("threeMonthsAgo") LocalDateTime threeMonthsAgo, Pageable pageable);

    //검색
    @Query(value = "SELECT a FROM Article a LEFT JOIN FETCH a.subPics sp " +
            "WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.genre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.ani_company) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(sp.realVoiceChar) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(sp.realChar) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(sp.korChar) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(sp.korVoiceChar) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY CASE " +
            "WHEN LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) THEN 1 " +
            "WHEN LOWER(a.genre) LIKE LOWER(CONCAT('%', :query, '%')) THEN 2 " +
            "WHEN LOWER(a.author) LIKE LOWER(CONCAT('%', :query, '%')) THEN 3 " +
            "WHEN LOWER(a.ani_company) LIKE LOWER(CONCAT('%', :query, '%')) THEN 4 " +
            "ELSE 5 END ASC")
    Page<Article> searchByQuery(@Param("query") String query, Pageable pageable);

   /* //articleId에 따른 tag
    @Query("SELECT a.tag.id, a.tag.tag FROM Article a WHERE a.id=:articleId")
    List <TagForm> findTagsByArticleId(@Param("articleId") Long articleId);
    */
}
