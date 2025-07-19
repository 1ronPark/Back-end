package umc.lightup.member.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.position.domain.Position;

@Entity
public class MemberPosition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    public MemberPosition(Member member, Position position) {
        this.member = member;
        this.position = position;
    }

    public MemberPosition() {

    }
}
