package com.example.webproject_maru.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.webproject_maru.entity.Qna;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long>{

    // 공지사항만 조회 (부모가 없는 글만)
    @Query("SELECT q FROM Qna q WHERE q.parentPost IS NULL AND q.isNotice = true ORDER BY q.appendTime DESC")
    Page<Qna> findNoticePosts(Pageable pageable);
    Page<Qna> findAllByParentPostIsNullAndIsNoticeFalseOrderByAppendTimeDesc(Pageable pageable);

    @Query("SELECT q FROM Qna q WHERE q.parentPost IS NULL AND (q.title LIKE %:keyword% OR q.author.nickname LIKE %:keyword%)")
    Page<Qna> searchByTitleOrAuthor(@Param("keyword") String keyword, Pageable pageable);

    
    List<Qna> findAllByParentPostIdOrderByAppendTimeAsc(Long parentId);
}
