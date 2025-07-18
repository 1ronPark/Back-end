package umc.lightup.strength.converter;

import umc.lightup.member.domain.Member;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.dto.StrengthRequestDTO;
import umc.lightup.strength.dto.StrengthResponseDTO;

import java.util.List;

public class StrengthConverter {

    public static StrengthResponseDTO.strengthListDTO toStrengthListDTO(List<String> strengths) {
        return StrengthResponseDTO.strengthListDTO.builder()
                .strengths(strengths)
                .build();
    }

    public static StrengthResponseDTO.createdStrengthResultDTO toCreatedStrengthResultDTO(String strengthName, Member member) {
        return StrengthResponseDTO.createdStrengthResultDTO.builder()
                .createdStrength(strengthName)
                .memberId(member.getId())
                .build();
    }

    public static Strength toStrength(StrengthRequestDTO.CreateStrengthDTO request, Member member) {
        String strengthName = request.getStrengthName();

        return Strength.createStrength(strengthName, true, member);
    }
}
