package umc.lightup.member.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.skill.domain.Skill;
import umc.lightup.strength.domain.Strength;

@Entity
public class MemberStrength extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "strength_id", nullable = false)
    private Strength strength;

    protected MemberStrength() {}

    public static MemberStrength createMemberStrength(Member member, Strength strength) {
        MemberStrength memberStrength = new MemberStrength();
        memberStrength.member = member;
        memberStrength.strength = strength;
        return memberStrength;
    }
}