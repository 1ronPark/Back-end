package umc.lightup.strength.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class StrengthResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class strengthListDTO {
        List<strengthResultDTO> strengths;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class strengthResultDTO {
        private Long strengthId;
        private String strengthName;
    }

    //커스텀 강점 생성 기능 삭제
/*    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createdStrengthResultDTO {
        String createdStrength;
        Long memberId;
    }*/

}
