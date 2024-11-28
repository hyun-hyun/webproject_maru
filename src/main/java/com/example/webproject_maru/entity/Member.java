package com.example.webproject_maru.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String email;
    @Column
    private String pswd;
    @Column
    private String gender;
    @Column
    private LocalDateTime appendDate;
    @Column
    private LocalDateTime updateDate;
    @Column
    private LocalDateTime loginDate;
    @Column
    private String role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Love> loves; // 회원의 찜 목록

    public String getNickname(){
        return nickname;
    }
}
