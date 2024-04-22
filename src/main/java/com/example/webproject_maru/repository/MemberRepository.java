package com.example.webproject_maru.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.webproject_maru.entity.Member;

public interface MemberRepository extends JpaRepository<Member,Long>{
    @Override
    ArrayList<Member> findAll();//Iterabel -> ArrayList수정

    List<Member> existsByNickname(String nickname);
    List<Member> existsByEmail(String email);

    Member findByEmail(String email);
}
