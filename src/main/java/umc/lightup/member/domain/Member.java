package umc.lightup.member.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.common.enums.Mbti;
import umc.lightup.common.enums.Role;

import java.time.LocalDate;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(unique = true, length = 20)
    private String nickname; // 추가

    @Column(nullable = false)
    private Boolean gender; // 수정

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 4)
    private Mbti mbti;

    @Column(unique = true, nullable = false, length = 30)
    private String email;

    @Column(length = 30)
    private String school;

    @Column(unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 80)
    private String career;

    private String profileImageUrl; // 추가, S3 필요

//    @Column(length = 32)
//    private String briefExplanation; // 추가
//
//    @Column(columnDefinition = "CHAR(4)")
//    private String Mbti; // 추가
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
//    private boolean receiveSuggestions = false;
}