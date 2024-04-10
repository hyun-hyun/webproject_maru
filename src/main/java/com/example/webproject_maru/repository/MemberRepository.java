package com.example.webproject_maru.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.webproject_maru.entity.Member;

public interface MemberRepository extends JpaRepository<Member,Long>{
    @Override
    ArrayList<Member> findAll();//Iterabel -> ArrayList수정

    Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);
}
