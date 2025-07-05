package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class MemberSkill extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}