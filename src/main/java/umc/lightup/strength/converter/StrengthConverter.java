package umc.lightup.strength.converter;

import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.dto.StrengthResponseDTO;

import java.util.List;

public class StrengthConverter {

    public static StrengthResponseDTO.strengthListDTO toStrengthListDTO(List<StrengthResponseDTO.strengthResultDTO> strengthResultDTOList) {
        return StrengthResponseDTO.strengthListDTO.builder()
                .strengths(strengthResultDTOList)
                .build();
    }

    public static StrengthResponseDTO.strengthResultDTO toStrengthResultDTO(Strength strength) {
        return StrengthResponseDTO.strengthResultDTO.builder()
                .strengthId(strength.getId())
                .strengthName(strength.getName())
                .build();
    }

    //커스텀 강점 생성 기능 삭제
/*    public static StrengthResponseDTO.createdStrengthResultDTO toCreatedStrengthResultDTO(String strengthName, Member member) {
        return StrengthResponseDTO.createdStrengthResultDTO.builder()
                .createdStrength(strengthName)
                .memberId(member.getId())
                .build();
    }

    public static Strength toStrength(StrengthRequestDTO.CreateStrengthDTO request, Member member) {
        String strengthName = request.getStrengthName();

        return Strength.createStrength(strengthName, true, member);
    }*/
}
