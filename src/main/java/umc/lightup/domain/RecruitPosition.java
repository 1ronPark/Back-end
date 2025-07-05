package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class RecruitPosition extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}