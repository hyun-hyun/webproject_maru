package com.example.webproject_maru.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.webproject_maru.dto.ArticleForm;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_id")
    private Long id;
    @Column
    private String catagory;
    @Column(unique = true)
    private String title;
    @Column
    private String genre;
    @Column
    private LocalDate broad_date;
    @Column
    private String ani_company;
    @Column
    private String author;
    @Column
    private String main_pic;
    @Column
    private String main_pic_name;

    //전체리뷰정리용
    @Column
    private Integer score;//안씀
    @Column
    private String score_reason;//ai로 요약적기
    @Column
    private Double avg_score=0.0;
    @Column
    private Integer c_score=0;

    //tag
    @OneToMany(mappedBy = "article")
    private List<Map_a_t> tags;

    @Transient  // 데이터베이스에 저장하지 않기 위해 Transient 사용
    private List<String> usedTags;  // 리뷰에서 사용한 태그 리스트 추가

    @Transient
    private List<String> allTags;

    @Column
    private String story;

    @OneToMany(mappedBy="article", cascade=CascadeType.ALL)
    private List<Review> reviews=new ArrayList<>();

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private LocalDateTime appendTime;
    @Column
    private LocalDateTime updateTime;
    

    public Article (Long id, String catagory, String title, String genre, LocalDate broad_date,String ani_company, String author, String story, Member member){
        this.id=id;
        this.catagory=catagory;
        this.title=title;
        this.genre=genre;
        this.broad_date=broad_date;
        this.ani_company=ani_company;
        this.author=author;
        this.story=story;
        this.member=member;
    }

    //게시글 수정내용 반영
    public void patch(ArticleForm form){
        //예외발생
        if(this.id != form.getId())
            throw new IllegalArgumentException("게시글 수정 실패! 잘못된 id가 입력됐습니다.");
        //객체갱신
        if(form.getTitle()!=null)//내용있을때
            this.title=form.getTitle();//반영
        if(form.getCatagory()!=null)
            this.catagory=form.getCatagory();
        if(form.getGenre()!=null)
            this.genre=form.getGenre();
        if(form.getBroad_date()!=null)
            this.broad_date=form.getBroad_date();
        if(form.getAni_company()!=null)
            this.ani_company=form.getCatagory();
        if(form.getAuthor()!=null)
            this.author=form.getAuthor();
        if(form.getStory()!=null)
            this.story=form.getStory();

    }


}
