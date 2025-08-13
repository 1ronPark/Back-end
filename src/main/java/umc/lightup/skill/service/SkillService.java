package umc.lightup.skill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.member.repository.MemberSkillRepository;
import umc.lightup.skill.converter.SkillConverter;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.dto.SkillResponseDTO;
import umc.lightup.skill.enums.SkillType;
import umc.lightup.skill.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final MemberSkillRepository memberSkillRepository;

    public List<SkillResponseDTO.skillResultDTO> getSkillsList(String positionName) {
        SkillType skillType = mapPositionToSkillType(positionName);
        List<Skill> skills = skillRepository.findAllOrderedBySkillType(skillType);

        return skills.stream()
                .map(SkillConverter::toSkillResultDTO)
                .toList();
    }

    private SkillType mapPositionToSkillType(String positionName) {
        return switch (positionName) {
            case "프론트엔드" -> SkillType.FRONTEND;
            case "백엔드" -> SkillType.BACKEND;
            case "디자인" -> SkillType.DESIGN;
            case "기획" -> SkillType.PLAN;
            case "마케팅" -> SkillType.MARKETING;
            default -> SkillType.COMMON;
        };
    }

    //커스텀 스킬 기능 삭제
/*    @Transactional
    public String createSkill(SkillRequestDTO.CreateSkillDTO request, Member member) {
        Skill newSkill = SkillConverter.toSkill(request, member);

        //생성 요청 받은 스킬 이름이 기본 제공 스킬 이름과 동일할 경우
        if (skillRepository.countByNameAndIsCustomFalse(newSkill.getName()) > 0) {
            throw new GeneralHandler(ErrorStatus.DUPLICATED_SKILL_NAME);
        }

        skillRepository.save(newSkill);

        MemberSkill memberSkill = MemberSkill.createMemberSkill(member, newSkill);
        memberSkillRepository.save(memberSkill);
        return newSkill.getName();
    }*/
}
