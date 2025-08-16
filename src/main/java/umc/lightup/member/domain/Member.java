package umc.lightup.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import umc.lightup.AppConfig;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.enums.Role;
import umc.lightup.member.service.CredentialQueryService;
import umc.lightup.school.domain.School;
import umc.lightup.school.domain.SchoolEmailVerification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) //Member에 대한 Equals 적용을 위해 필요함
public class Member extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include //Id만 비교하면 끝이라서 Include와 onlyExplicitlyIncluded = true 설정 진행
    private Long id;

    @Column(length = 20) //소셜로그인 회원가입 시 안 들어올 수 있음
    private String name;

    @Column(unique = true, length = 20)
    private String nickname; // 추가

    private Boolean gender; // 수정

    private Integer age;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private Role role;

    /**
     * E +8,
     * N +4,
     * F +2,
     * P +1,
     * 총 0(ISTJ)~15(ENFP)로 표현
     * */
    @Column(columnDefinition = "BIT(4)")
    private Byte mbti;

    @Column(unique = true, nullable = false, length = 30)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @Column(unique = true, length = 20)
    private String phoneNumber;

    @Setter
    @Column(length = 80)
    private String profileTitle;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String selfIntroduce;

    @Setter
    private String profileImageUrl; // 추가, S3 필요

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberRegion> memberRegions = new ArrayList<>();

    public void addMemberRegion(MemberRegion memberRegion) {
        this.memberRegions.add(memberRegion);
        memberRegion.assignMember(this);
    }

//    @OneToMany(mappedBy = "member", orphanRemoval = true)
//    private List<Credential> credentials;

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<MemberSkill> skills;
//
//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<MemberStrength> strengths;
//
//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<Portfolio> portfolios;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()->"ROLE_"+role);
    }

    @Override
    public String getPassword() {
        // 사실 쓰지 않는다는데 비워놓기는 찝찝해 이상한 짓거리 해 놓긴 함
        // Bean을 수동으로 가져와 Credential 알아내는 코드
        // AppConfig 클래스가 필요함
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        CredentialQueryService credentialQueryService = applicationContext.getBean(CredentialQueryService.class);
        return credentialQueryService.findByEmail(getEmail()).getCredential();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    public String getNameNotNull() {
        if (getNickname() != null) return getNickname();
        else if (getName() != null) return getName();
        else return getEmail();
    }

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