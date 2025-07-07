package umc.lightup.member.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;

@Entity
public class Portfolio extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 30)
    private String name;

    @Column(nullable = false, length = 255)
    private String fileUrl;
}