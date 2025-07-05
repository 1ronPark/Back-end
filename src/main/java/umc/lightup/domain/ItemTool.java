package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class ItemTool extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;
}