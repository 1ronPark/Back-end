package umc.lightup.strength.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.member.repository.MemberStrengthRepository;
import umc.lightup.strength.converter.StrengthConverter;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.dto.StrengthResponseDTO;
import umc.lightup.strength.enums.StrengthType;
import umc.lightup.strength.repository.StrengthRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StrengthService {

    private final StrengthRepository strengthRepository;
    private final MemberStrengthRepository memberStrengthRepository;

    public List<StrengthResponseDTO.strengthResultDTO> getStrengthsList(String positionName) {
        StrengthType strengthType = mapPositionToStrengthType(positionName);
        List<Strength> strengths = strengthRepository.findAllOrderedByStrengthType(strengthType);

        return strengths.stream()
                .map(StrengthConverter::toStrengthResultDTO)
                .toList();
    }

    private StrengthType mapPositionToStrengthType(String positionName) {
        return switch (positionName) {
            case "프론트엔드" -> StrengthType.FRONTEND;
            case "백엔드" -> StrengthType.BACKEND;
            case "디자인" -> StrengthType.DESIGN;
            case "기획" -> StrengthType.PLAN;
            case "마케팅" -> StrengthType.MARKETING;
            default -> StrengthType.COMMON;
        };
    }

    //커스텀 강점 생성 기능 삭제
/*    @Transactional
    public String createStrength(StrengthRequestDTO.CreateStrengthDTO request, Member member) {
        Strength newStrength = StrengthConverter.toStrength(request, member);

        //생성 요청 받은 강점 이름이 기본 제공 강점 이름과 동일할 경우
        if (strengthRepository.countByNameAndIsCustomFalse(newStrength.getName()) > 0) {
            throw new GeneralHandler(ErrorStatus.DUPLICATED_STRENGTH_NAME);
        }

        strengthRepository.save(newStrength);

        MemberStrength memberStrength = MemberStrength.createMemberStrength(member, newStrength);
        memberStrengthRepository.save(memberStrength);
        return newStrength.getName();
    }*/
}
