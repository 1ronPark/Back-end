package umc.lightup.skill.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.skill.converter.SkillConverter;
import umc.lightup.skill.dto.SkillRequestDTO;
import umc.lightup.skill.dto.SkillResponseDTO;
import umc.lightup.skill.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skills")
public class SkillRestController {

    private final SkillService skillService;
    private final MemberCommandService memberCommandService;

    @GetMapping
    @Operation(
            summary = "유저의 스킬 조회 API",
            description = "유저가 스킬을 선택하기 전에 드롭다운으로 스킬을 조회하는 API입니다."
    )
    public ApiResponse<SkillResponseDTO.skillListDTO> getSkills() {
        List<String> skillsList = skillService.getSkillsList();
        return ApiResponse.onSuccess(SkillConverter.toSkillListDTO(skillsList));
    }

    @PostMapping
    @Operation(
            summary = "유저의 커스텀 스킬 생성 API",
            description = "유저가 직접 스킬을 생성하고 선택하는 API입니다. 유저가 생성한 스킬은 생성과 동시에 선택됩니다."
    )
    public ApiResponse<SkillResponseDTO.createdSkillResultDTO> createCustomSkill(Authentication authentication,
                                                                                 @RequestBody SkillRequestDTO.CreateSkillDTO request) {
        Member member = memberCommandService.getMember(authentication.getName());
        String skillName = skillService.createSkill(request, member);

        return ApiResponse.onSuccess(SkillConverter.toCreatedSkillResultDTO(skillName, member));
    }
}
