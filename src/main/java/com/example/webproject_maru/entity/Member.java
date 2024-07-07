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
    @Column
    private Long id;
    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String email;
    @Column
    private String pswd;
    @Column
    private LocalDateTime appendDate;
    @Column
    private LocalDateTime updateDate;
    @Column
    private LocalDateTime loginDate;
    @Column
    private String role;


    public String getNickname(){
        return nickname;
    }
}
