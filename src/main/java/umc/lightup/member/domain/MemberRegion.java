package umc.lightup.member.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.region.domain.Region;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRegion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String siDo;

    private String siGunGu;

    public void assignMember(Member member) { this.member = member; }
}