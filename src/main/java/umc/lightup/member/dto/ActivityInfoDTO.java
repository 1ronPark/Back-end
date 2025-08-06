package umc.lightup.member.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityInfoDTO {
    private String name;
    // 일 value는 아무거나 넣어서 진행
    private LocalDate startDate;

    private Boolean hasEndDate;
    // 기간이 아닌 특정 시점이면 null이 들어감
    private LocalDate endDate;
}