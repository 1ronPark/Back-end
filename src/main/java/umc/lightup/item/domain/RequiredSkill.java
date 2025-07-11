package umc.lightup.item.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.skill.domain.Skill;

@Entity
public class RequiredSkill extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}