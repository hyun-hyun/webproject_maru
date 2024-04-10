package com.example.webproject_maru.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nickname;
    private String email;
    private String pswd;
    // @Column
    // private LocalDateTime appendDate;
    // @Column
    // private LocalDateTime updateDate;
    // @Column
    // private String role;
    public Member toEntity(){
        return new Member(id, nickname, email, pswd);
    }
}
