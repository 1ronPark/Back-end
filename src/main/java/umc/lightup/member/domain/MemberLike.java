package umc.lightup.member.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;

@Entity
public class MemberLike extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;
}