package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class MemberLike extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;
}