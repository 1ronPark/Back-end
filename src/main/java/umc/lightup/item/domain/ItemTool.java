package umc.lightup.item.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.tool.domain.Tool;

@Entity
public class ItemTool extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;
}