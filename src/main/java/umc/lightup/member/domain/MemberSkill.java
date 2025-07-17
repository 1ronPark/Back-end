package umc.lightup.member.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.skill.domain.Skill;

@Entity
public class MemberSkill extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    protected MemberSkill() {}

    public static MemberSkill createMemberSkill(Member member, Skill skill) {
        MemberSkill memberSkill = new MemberSkill();
        memberSkill.member = member;
        memberSkill.skill = skill;
        return memberSkill;
    }
}