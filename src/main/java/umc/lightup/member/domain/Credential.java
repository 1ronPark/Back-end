package umc.lightup.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    private String credential;
}