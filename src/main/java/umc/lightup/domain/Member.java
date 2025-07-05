package umc.lightup.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
public class Member extends BaseEntity {
    @Column(length = 20)
    private String name;

//    @Column(unique = true, length = 20)
//    private String nickname; // 추가

    @Column(nullable = false)
    private Boolean gender; // 수정

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(unique = true, nullable = false, length = 30)
    private String email;

    @Column(length = 30)
    private String school;

    @Column(unique = true, nullable = false, length = 20)
    private String phoneNumber;

//    @Column(length = 32)
//    private String briefExplanation; // 추가
//
//    @Column(columnDefinition = "CHAR(4)")
//    private String Mbti; // 추가
//
//    private String picture; // 추가, S3 필요
//
//    private boolean active = true;
//
//    //---------------------------------------------------
//
//    private String preferredLocation;
//
//    @Column(columnDefinition = "TEXT")
//    private String selfIntroduction;
//
//    @Column(columnDefinition = "TEXT")
//    private String rewards;
//
//    private boolean receiveSuggestions = false;
}