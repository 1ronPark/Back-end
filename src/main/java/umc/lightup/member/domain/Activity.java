package umc.lightup.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(length = 72, nullable = false)
    private String name;

    @Column(nullable = false) // 일 value는 아무거나 넣어서 진행
    private LocalDate startDate;

    @Column // 기간이 아닌 특정 시점이면 null이 들어감
    private LocalDate endDate;
}