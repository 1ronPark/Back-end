package umc.lightup.item.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.position.domain.Position;

@Entity
public class RecruitPosition extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}