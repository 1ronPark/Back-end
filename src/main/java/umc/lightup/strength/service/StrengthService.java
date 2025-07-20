package umc.lightup.strength.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberStrength;
import umc.lightup.member.repository.MemberStrengthRepository;
import umc.lightup.strength.converter.StrengthConverter;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.dto.StrengthRequestDTO;
import umc.lightup.strength.repository.StrengthRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StrengthService {

    private final StrengthRepository strengthRepository;
    private final MemberStrengthRepository memberStrengthRepository;

    public List<String> getStrengthsList() {
        List<Strength> strengths = strengthRepository.findBasicStrengths();
        return strengths.stream()
                .map(Strength::getName)
                .toList();
    }

    @Transactional
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
    }
}
