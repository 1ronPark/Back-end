package umc.lightup.skill.domain;

import jakarta.persistence.*;
import lombok.*;

import umc.lightup.skill.enums.SkillType;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SkillType skillType;

    //커스텀 스킬 생성 기능 삭제
/*    @Column(nullable = false)
    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    public static Skill createSkill(String name, boolean isCustom, Member owner) {
        Skill skill = new Skill();
        skill.name = name;
        skill.isCustom = isCustom;
        skill.owner = owner;
        return skill;
    }*/
}