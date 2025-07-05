package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class RequiredSkill extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}