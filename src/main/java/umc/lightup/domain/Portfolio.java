package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class Portfolio extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 30)
    private String name;

    @Column(nullable = false, length = 255)
    private String fileUrl;
}