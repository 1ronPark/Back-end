package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.SuccessStatus;
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
        //반환값을 설정하는 게 맞나? 204 No Content를 반환하는 것도 괜찮았을 것 같은데... 특히 이메일 변경되면 로그아웃이 필수가 되어 버렸기 때문에...
        //반환값은 단순 디버그용 그 이상이 아니게 될 수도 있음(정작 Test 코드에선 return 값을 아주 잘 활용 중)
        //여기서 주의할 점은 이메일 변경되면 로그아웃이 필수이지만 백엔드에서는 아무런 처리를 해 주지 않는다는 것. 알아서 기존 token 지우고 다시 로그인 해야 함.
        //로그아웃이 필수인 이유가 jwt에 이메일을 저장하기 때문인데, 이 때문에 A와 B가 서로의 비밀번호를 몰라도 서로 이메일을 바꾸면 A는 B 계정에, B는 A 계정에 접속할 수 있음.
        String email = authentication.getName();
        return ApiResponse.onSuccess(MemberResponseDTO.toMyInfoDTO(memberCommandService.putMember(email, request)));
    }

    @PostMapping("/password/change")
    @ResponseStatus(HttpStatus.NO_CONTENT) //명시적으로 쓰기 싫었는데 안 쓰니 200 OK가 나가버리네...
    @Operation(
            summary = "비밀번호 변경 API",
            description = "비밀번호를 변경하는 API입니다. 로그아웃이 필요하지는 않습니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<Void> passwordChange(Authentication authentication,
                                            @RequestBody @Valid MemberRequestDTO.PasswordChangeRequestDTO request) {
        String email = authentication.getName();
        credentialQueryService.updatePasswordByEmail(email, request);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
        //null 반환이 과연 옳은가? 물론 이 null 안 쓰려면 응답 통일 형식부터 갈아엎어야 하는 대공사가 필요하긴 함
    }

    @PostMapping("/email/exist")
    @Operation(
            summary = "이메일 존재 여부 확인 API",
            description = "이메일 존재 여부를 확인하는 API입니다. 회원가입 시 중복 여부나 로그인 시 메일 존재 여부 확인 등의 용도로 사용할 수 있습니다."
    )
    public ApiResponse<MemberResponseDTO.EmailExistResultDTO> emailExist(@RequestParam @NotBlank @Email String email) {
        //항상 DTO 쓰다 여기에만 RequestParam 쓰니 이상하긴 하네...
        //아 그냥 ApiResponse<Boolean> 반환해버리고 싶다
        boolean emailExist = memberCommandService.isEmailExist(email);
        return ApiResponse.onSuccess(new MemberResponseDTO.EmailExistResultDTO(emailExist));
    }
}