package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class Item extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(length = 40, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String introduce;

    @Column(name = "project_status", length = 50, nullable = false)
    private String projectStatus;

    @Column(length = 50, nullable = false)
    private String collaboration;

    @Column(length = 30, nullable = false)
    private String address;

    @Column(length = 30, nullable = false)
    private String office;

    @Column(name = "prefer_mbti", nullable = false)
    private Boolean preferMbti;
}