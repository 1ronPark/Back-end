package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.service.MemberCommandService;

import static umc.lightup.member.dto.MemberResponseDTO.memberPositionDeleteResultDTOBuilder;
import static umc.lightup.member.dto.MemberResponseDTO.memberPositionResultDTOBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberRestController {
    private final MemberCommandService memberCommandService;


    @PostMapping("/join")
    @Operation(summary = "유저 비밀번호 회원가입 API",description = "유저가 비밀번호로 회원가입하는 API입니다.")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(@RequestBody @Valid MemberRequestDTO.JoinDto request) {
        Member member = memberCommandService.joinMember(request);
        return ApiResponse.onSuccess(MemberResponseDTO.joinResultDTOBuilder()
                .memberId(member.getId())
                .createdAt(member.getCreatedAt())
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "유저 비밀번호 로그인 API",description = "유저가 비밀번호로 로그인하는 API입니다.")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login
            (@RequestBody @Valid MemberRequestDTO.PasswordLoginRequestDTO request) {
        return ApiResponse.onSuccess(memberCommandService.loginMember(request));
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 정보 조회 API",
            description = "자신의 회원 정보를 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MyInfoDTO> getMyInfo(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        return ApiResponse.onSuccess(MemberResponseDTO.toMyInfoDTO(member));
    }

    @GetMapping("/{memberId}")
    @Operation(
            summary = "회원(타인) 정보 조회 API",
            description = "타인의 회원 정보를 조회하는 API입니다." +
                    " 프로젝트에 참여했을 때 일부 데이터를 추가로 공개하는 작업은 아직 진행하지 않았습니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> getMemberInfo(Authentication authentication,
                                                                  @PathVariable("memberId") long id) {
        String email = null;
        if (authentication != null)
            email = authentication.getName();
        return ApiResponse.onSuccess(memberCommandService.getMember(id, email));
    }

    @PostMapping("/position")
    public ApiResponse<MemberResponseDTO.MemberPositionResultDTO> selectMemberPosition(Authentication authentication, @RequestParam String positionName) {
        String userName = authentication.getName();
        Member member = memberCommandService.getMember(userName);

        memberCommandService.selectPosition(member.getId(), positionName);

        return ApiResponse.onSuccess(memberPositionResultDTOBuilder()
                .memberName(userName)
                .positionName(positionName)
                .build());
    }

    @DeleteMapping("/position")
    public ApiResponse<MemberResponseDTO.MemberPositionDeleteResultDTO> deleteMemberPosition(Authentication authentication, @RequestParam String positionName) {
        String userName = authentication.getName();
        Member member = memberCommandService.getMember(userName);

        memberCommandService.deletePosition(member.getId(), positionName);

        return ApiResponse.onSuccess(memberPositionDeleteResultDTOBuilder()
                .memberName(userName)
                .deletePositionName(positionName)
                .build());
    }

    @PostMapping("/skills")
    @Operation(
            summary = "스킬 선택 API",
            description = "유저가 자신의 스킬을 선택하는 API입니다."
    )
    public ApiResponse<MemberResponseDTO.selectSkillResultDTO> selectSkill(Authentication authentication, @RequestBody MemberRequestDTO.MemberSkillSelectRequestDTO request) {
        Long skillId = request.getSkillId();
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        String skillName = memberCommandService.selectSkill(skillId, member);
        return ApiResponse.onSuccess(MemberConverter.toSelectSkillResultDTO(skillName, member));
    }

    @PostMapping("/strengths")
    @Operation(
            summary = "강점 선택 API",
            description = "유저가 자신의 강점을 선택하는 API입니다."
    )
    public ApiResponse<MemberResponseDTO.selectStrengthResultDTO> selectStrength(Authentication authentication, @RequestBody MemberRequestDTO.MemberStrengthSelectRequestDTO request) {
        Long strengthId = request.getStrengthId();
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        String strengthName = memberCommandService.selectStrength(strengthId, member);
        return ApiResponse.onSuccess(MemberConverter.toSelectStrengthResultDTO(strengthName, member));
    }
}