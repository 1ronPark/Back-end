package umc.lightup.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import umc.lightup.AppConfig;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.enums.Mbti;
import umc.lightup.member.enums.Role;
import umc.lightup.member.service.CredentialQueryServiceImpl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

//    @OneToMany(mappedBy = "member", orphanRemoval = true)
//    private List<Credential> credentials;

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
        CredentialQueryServiceImpl credentialQueryService = applicationContext.getBean(CredentialQueryServiceImpl.class);
        return credentialQueryService.findByEmail(getEmail()).getCredential();
    }

    @Override
    public String getUsername() {
        return getEmail();
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