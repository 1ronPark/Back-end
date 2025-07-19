package umc.lightup.member.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.enums.CredentialType;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential extends BaseEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CredentialType credentialType;

    @Setter //별로 열고 싶지는 않았는데...
    @Column(nullable = false)
    private String credential;
}