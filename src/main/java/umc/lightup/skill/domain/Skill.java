package umc.lightup.skill.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.domain.Member;

@Getter
@Entity
public class Skill extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = false)
    private String name;

    @Column(nullable = false)
    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    protected Skill() {}

    public static Skill createSkill(String name, boolean isCustom, Member owner) {
        Skill skill = new Skill();
        skill.name = name;
        skill.isCustom = isCustom;
        skill.owner = owner;
        return skill;
    }
}