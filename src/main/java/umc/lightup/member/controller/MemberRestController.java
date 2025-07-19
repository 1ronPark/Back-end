package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.service.CredentialQueryService;
import umc.lightup.member.service.MemberCommandService;

import static umc.lightup.member.dto.MemberResponseDTO.memberPositionDeleteResultDTOBuilder;
import static umc.lightup.member.dto.MemberResponseDTO.memberPositionResultDTOBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberRestController {
    private final MemberCommandService memberCommandService;
    private final CredentialQueryService credentialQueryService;


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

    @PutMapping("/me")
    @Operation(
            summary = "회원 정보 변경 API",
            description = "회원 정보를 변경하는 API입니다. 이메일도 변경이 가능하나 인증과 관련된 사항이기에 반드시 logout을 시행해 주셔야 합니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MyInfoDTO> changeMemberInfo(Authentication authentication,
                                                                     @RequestBody @Valid MemberRequestDTO.ChangeDto request) {
        //반환값을 설정하는 게 맞나? 201 No Content를 반환하는 것도 괜찮았을 것 같은데... 특히 이메일 변경되면 로그아웃이 필수가 되어 버렸기 때문에...
        //반환값은 단순 디버그용 그 이상이 아니게 될 수도 있음(정작 Test 코드에선 return 값을 아주 잘 활용 중)
        String email = authentication.getName();
        return ApiResponse.onSuccess(MemberResponseDTO.toMyInfoDTO(memberCommandService.putMember(email, request)));
    }

    @PostMapping("/password/change")
    @Operation(
            summary = "비밀번호 변경 API",
            description = "비밀번호를 변경하는 API입니다. 로그아웃이 필요하지는 않습니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.PasswordChangeResultDTO> passwordChange(Authentication authentication,
                                                                                 @RequestBody @Valid MemberRequestDTO.PasswordChangeRequestDTO request) {
        //반환값을 설정하는 게 맞나? 201 No Content를 반환하는 것도 괜찮았을 것 같은데...
        String email = authentication.getName();
        Credential credential = credentialQueryService.updatePasswordByEmail(email, request);
        //나중에 이거 어차피 converter 형식으로 바꿔야 함, 아직 converter 클래스가 없는 버전이라 일단은 이렇게 둠(물론 이렇게 두는 방식이 좋은 방식은 아니지만 귀찮아서...)
        return ApiResponse.onSuccess(new MemberResponseDTO.PasswordChangeResultDTO(credential.getMember().getId(), credential.getUpdatedAt()));
    }
}