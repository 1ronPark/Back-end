package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
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
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(@RequestBody @Valid MemberRequestDTO.JoinDto request) {
        Member member = memberCommandService.joinMember(request);
        return ApiResponse.onSuccess(MemberResponseDTO.joinResultDTOBuilder()
                .memberId(member.getId())
                .createdAt(member.getCreatedAt())
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "유저 로그인 API",description = "유저가 로그인하는 API입니다.")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login
            (@RequestBody @Valid MemberRequestDTO.PasswordLoginRequestDTO request) {
        return ApiResponse.onSuccess(memberCommandService.loginMember(request));
    }

    @GetMapping("/info")
    @Operation(
            summary = "회원 정보 조회 API",
            description = "테스트용 회원 정보를 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> getMemberInfo(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        return ApiResponse.onSuccess(MemberResponseDTO.toMemberInfoDTO(member));
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
}