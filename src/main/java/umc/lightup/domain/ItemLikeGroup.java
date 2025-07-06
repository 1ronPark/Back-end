package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class ItemLikeGroup extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 20, nullable = false)
    private String name;
}