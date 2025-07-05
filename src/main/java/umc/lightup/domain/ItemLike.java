package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class ItemLike extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}