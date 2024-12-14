package com.example.webproject_maru.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.webproject_maru.dto.ArticleForm;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="article")
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_id")
    private Long id;
    @Column
    private String category;
    @Column(unique = true)
    private String title;
    @Column
    private String genre;
    @Column
    private LocalDate broaddate;
    @Column
    private String ani_company;
    @Column
    private String author;
    @Column
    private String main_pic;
    @Column
    private String main_pic_name;


    @Column
    private String score_reason;//ai로 요약적기
    @Column
    private Double avgscore=0.0;
    @Column
    private Long cscore=0L;

    //tag
    @OneToMany(mappedBy = "article")
    private List<Map_a_t> tags;

    @Column
    private String story;

    @OneToMany(mappedBy="article", cascade=CascadeType.ALL)
    private List<Review> reviews=new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "sub_pics", joinColumns = @JoinColumn(name = "article_id"))
    private List<SubPic> subPics = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Map_a_t> map_a_ts = new ArrayList<>();
/* 
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "pic", column = @Column(name = "sub_pic1_pic")),
        @AttributeOverride(name = "name", column = @Column(name = "sub_pic1_name")),
        @AttributeOverride(name = "realChar", column = @Column(name = "sub_pic1_real_char")),
        @AttributeOverride(name = "realVoiceChar", column = @Column(name = "sub_pic1_real_voice_char")),
        @AttributeOverride(name = "korChar", column = @Column(name = "sub_pic1_kor_char")),
        @AttributeOverride(name = "korVoiceChar", column = @Column(name = "sub_pic1_kor_voice_char"))
    })
    private SubPic subPic1;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "pic", column = @Column(name = "sub_pic2_pic")),
        @AttributeOverride(name = "name", column = @Column(name = "sub_pic2_name")),
        @AttributeOverride(name = "realChar", column = @Column(name = "sub_pic2_real_char")),
        @AttributeOverride(name = "realVoiceChar", column = @Column(name = "sub_pic2_real_voice_char")),
        @AttributeOverride(name = "korChar", column = @Column(name = "sub_pic2_kor_char")),
        @AttributeOverride(name = "korVoiceChar", column = @Column(name = "sub_pic2_kor_voice_char"))
    })
    private SubPic subPic2;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "pic", column = @Column(name = "sub_pic3_pic")),
        @AttributeOverride(name = "name", column = @Column(name = "sub_pic3_name")),
        @AttributeOverride(name = "realChar", column = @Column(name = "sub_pic3_real_char")),
        @AttributeOverride(name = "realVoiceChar", column = @Column(name = "sub_pic3_real_voice_char")),
        @AttributeOverride(name = "korChar", column = @Column(name = "sub_pic3_kor_char")),
        @AttributeOverride(name = "korVoiceChar", column = @Column(name = "sub_pic3_kor_voice_char"))
    })
    private SubPic subPic3;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "pic", column = @Column(name = "sub_pic4_pic")),
        @AttributeOverride(name = "name", column = @Column(name = "sub_pic4_name")),
        @AttributeOverride(name = "realChar", column = @Column(name = "sub_pic4_real_char")),
        @AttributeOverride(name = "realVoiceChar", column = @Column(name = "sub_pic4_real_voice_char")),
        @AttributeOverride(name = "korChar", column = @Column(name = "sub_pic4_kor_char")),
        @AttributeOverride(name = "korVoiceChar", column = @Column(name = "sub_pic4_kor_voice_char"))
    })
    private SubPic subPic4;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "pic", column = @Column(name = "sub_pic5_pic")),
        @AttributeOverride(name = "name", column = @Column(name = "sub_pic5_name")),
        @AttributeOverride(name = "realChar", column = @Column(name = "sub_pic5_real_char")),
        @AttributeOverride(name = "realVoiceChar", column = @Column(name = "sub_pic5_real_voice_char")),
        @AttributeOverride(name = "korChar", column = @Column(name = "sub_pic5_kor_char")),
        @AttributeOverride(name = "korVoiceChar", column = @Column(name = "sub_pic5_kor_voice_char"))
    })
    private SubPic subPic5;
*/
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private LocalDateTime appendTime;
    @Column
    private LocalDateTime updateTime;
    

    public Article (Long id, String category, String title, String genre, LocalDate broaddate,String ani_company, String author, String story, Member member){
        this.id=id;
        this.category=category;
        this.title=title;
        this.genre=genre;
        this.broaddate=broaddate;
        this.ani_company=ani_company;
        this.author=author;
        this.story=story;
        this.member=member;
    }

    //게시글 수정내용 반영
    public void patch(ArticleForm form, Member member){
        //예외발생
        if(this.id != form.getId())
            throw new IllegalArgumentException("게시글 수정 실패! 잘못된 id가 입력됐습니다.");
        //객체갱신
        if(form.getTitle()!=null)//내용있을때
            this.title=form.getTitle();//반영
        if(form.getCategory()!=null)
            this.category=form.getCategory();
        if(form.getGenre()!=null)
            this.genre=form.getGenre();
        if(form.getBroaddate()!=null)
            this.broaddate=form.getBroaddate();
        if(form.getAni_company()!=null)
            this.ani_company=form.getAni_company();
        if(form.getAuthor()!=null)
            this.author=form.getAuthor();
        if(form.getMemberId()!=null)
            this.member=member;
        if(form.getStory()!=null)
            this.story=form.getStory();

    }


}
