package com.example.webproject_maru.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DB가 ID자동생성
    private Long id;

    @Column
    private String tag;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<Map_a_t> articles=new ArrayList<>();

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<Map_r_t> reviews=new ArrayList<>();

    public Tag(String name){
        this.tag=name;
    }

   
}
