package umc.lightup.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PortfolioInfoDTO {
    @NotEmpty
    @Size(max = 30)
    private String name;
    @NotEmpty
    private String fileUrl;
}