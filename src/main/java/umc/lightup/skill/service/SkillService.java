package umc.lightup.skill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberSkill;
import umc.lightup.member.repository.MemberSkillRepository;
import umc.lightup.skill.converter.SkillConverter;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.dto.SkillRequestDTO;
import umc.lightup.skill.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final MemberSkillRepository memberSkillRepository;

    public List<String> getSkillsList() {
        List<Skill> skills = skillRepository.findBasicSkills();
        return skills.stream()
                .map(Skill::getName)
                .toList();
    }

    @Transactional
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
    }
}
