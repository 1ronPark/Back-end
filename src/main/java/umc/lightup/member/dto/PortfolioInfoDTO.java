package umc.lightup.member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PortfolioInfoDTO {
    private String name;
    private String fileUrl;
}