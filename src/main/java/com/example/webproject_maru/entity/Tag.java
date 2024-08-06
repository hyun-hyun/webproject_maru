package com.example.webproject_maru.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DB가 ID자동생성
    private Long id;

    @Column
    private String tag;

    @OneToMany(mappedBy = "tag")
    private List<Map_a_t> articles;

    @OneToMany(mappedBy = "tag")
    private List<Map_r_t> reviews;
}
