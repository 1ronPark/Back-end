package umc.lightup.item.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import umc.lightup.item.enums.ItemApplyStatus;
import umc.lightup.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * item owner의 member에 대한 제안인지, member가 item owner에게 지원한 건지 구분.
     * <p>
     * {@code true} item owner가 member에게 제안한 것
     * <p>
     * {@code false} member가 item에 지원한 것, 기본값
     */
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean fromOwner = false;

    @Setter
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PENDING'")
    private ItemApplyStatus status; // 지원 상태: 대기, 수락, 거절

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

}

