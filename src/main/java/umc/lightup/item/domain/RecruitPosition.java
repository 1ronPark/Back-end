package umc.lightup.item.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.enums.Mbti;
import umc.lightup.position.domain.Position;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitPosition extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(length = 3000, nullable = false)
    private String mainTasks;

    @Column(length = 1000)
    private String preferentialTreatment;

    @Column(length = 100)
    private String preferMbti;

    @Column(nullable = false)
    private Integer recruitNumber;
}