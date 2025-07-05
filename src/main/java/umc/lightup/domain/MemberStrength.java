package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class MemberStrength extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "strength_id", nullable = false)
    private Strength strength;
}