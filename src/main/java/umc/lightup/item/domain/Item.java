package umc.lightup.item.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.domain.Member;

@Entity
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 40, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String introduce;

    @Column(length = 80, nullable = false)
    private String description;

    @Column(name = "project_status", length = 50, nullable = false)
    private String projectStatus;

    @Column(length = 50, nullable = false)
    private String collaboration;

    @Column(length = 30, nullable = false)
    private String address;

    @Column(length = 30, nullable = false)
    private Boolean office;

    @Column(name = "prefer_mbti", nullable = false, length = 30)
    private String preferMbti;
}