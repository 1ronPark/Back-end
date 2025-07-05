package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class ItemLikeGroupItem extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ItemLikeGroup group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}