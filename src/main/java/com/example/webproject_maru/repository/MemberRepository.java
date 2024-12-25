package com.example.webproject_maru.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.entity.Qna;

public interface MemberRepository extends JpaRepository<Member,Long>{
    @Override
    ArrayList<Member> findAll();//Iterabel -> ArrayList수정

    List<Member> existsByNickname(String nickname);
    List<Member> existsByEmail(String email);

    Member findByEmail(String email);

    Optional<Member> findByNickname(String nickname);
}
