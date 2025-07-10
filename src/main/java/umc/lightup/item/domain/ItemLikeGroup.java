package umc.lightup.item.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.domain.Member;

@Entity
public class ItemLikeGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 20, nullable = false)
    private String name;
}