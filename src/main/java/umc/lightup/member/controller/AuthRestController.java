package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.service.CredentialQueryService;
import umc.lightup.member.service.MemberCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members") // 사실 이것도 같이 변경하는 게 맞으나 프론트에서 이미 작업이 이루어져 어려울듯...
public class AuthRestController {
    private final MemberCommandService memberCommandService;
    private final CredentialQueryService credentialQueryService;


    @PostMapping("/join")
    @Operation(summary = "유저 비밀번호 회원가입 API",description = "유저가 비밀번호로 회원가입하는 API입니다.")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(@RequestBody @Valid MemberRequestDTO.JoinDto request) {
        Member member = credentialQueryService.joinMember(request);
        return ApiResponse.onSuccess(MemberResponseDTO.joinResultDTOBuilder()
                .memberId(member.getId())
                .createdAt(member.getCreatedAt())
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "유저 비밀번호 로그인 API",description = "유저가 비밀번호로 로그인하는 API입니다.")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login
            (@RequestBody @Valid MemberRequestDTO.PasswordLoginRequestDTO request) {
        return ApiResponse.onSuccess(credentialQueryService.loginMember(request));
    }

    @PostMapping("/join/{oauth}")
    @Operation(summary = "유저 소셜로그인을 통한 회원가입 API",description = "유저가 소셜로그인으로 회원가입하는 API입니다.")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> joinByOAuth(
            @PathVariable("oauth") CredentialType oauth,
            @RequestParam("authCode") @NotBlank String authCode) {
        Member member = switch (oauth) {
            case GOOGLE -> credentialQueryService.joinMemberByGoogle(authCode);
            case KAKAO -> credentialQueryService.joinMemberByKakao(authCode);
            case PASSWORD -> throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        };
        return ApiResponse.onSuccess(MemberResponseDTO.joinResultDTOBuilder()
                .memberId(member.getId())
                .createdAt(member.getCreatedAt())
                .build());
    }

    @PostMapping("/login/{oauth}")
    @Operation(summary = "유저 소셜로그인 API",description = "유저가 소셜로그인하는 API입니다.")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> loginByOAuth
            (@PathVariable("oauth") CredentialType oauth,
             @RequestParam("authCode") @NotBlank String authCode) {
        MemberResponseDTO.LoginResultDTO loginResult = switch (oauth) {
            case GOOGLE -> credentialQueryService.loginMemberByGoogle(authCode);
            case KAKAO -> credentialQueryService.loginMemberByKakao(authCode);
            case PASSWORD -> throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        };
        return ApiResponse.onSuccess(loginResult);
    }

    @PostMapping("login/path/{oauth}")
    @Operation(summary = "유저 소셜로그인 방법 추가 API",
            description = "유저가 로그인 방법을 추가하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<Void> addLogin
            (Authentication authentication,
             @PathVariable("oauth") CredentialType oauth,
             @RequestParam("authCode") @NotBlank String authCode) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        switch (oauth) {
            case GOOGLE -> credentialQueryService.addGoogleLogin(member, authCode);
            case KAKAO -> credentialQueryService.addKakaoLogin(member, authCode);
            case PASSWORD -> credentialQueryService.addPasswordLogin(member, authCode);
        };
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @GetMapping("login/path")
    @Operation(summary = "유저 소셜로그인 방법 확인 API",
            description = "유저가 로그인 방법을 확인하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.CredentialInfoResultDTO> checkLogin
            (Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(credentialQueryService.getMemberCredentials(email));
    }

    @DeleteMapping("login/path/{oauth}")
    @Operation(summary = "유저 소셜로그인 방법 삭제 API",
            description = "유저가 로그인 방법을 삭제하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<Void> removeLogin
            (Authentication authentication,
             @PathVariable("oauth") CredentialType oauth) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        credentialQueryService.removeCredential(member, oauth);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @PostMapping("/password/change")
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

    @PostMapping("/password/check")
    @Operation(
            summary = "비밀번호 확인 API",
            description = "비밀번호가 일치하는지 확인하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<Void> passwordCheck(Authentication authentication,
                                           @RequestBody @Valid MemberRequestDTO.PasswordCheckRequestDTO request) {
        String email = authentication.getName();
        credentialQueryService.checkPasswordByEmail(email,request.getPassword());
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
        //null 반환이 과연 옳은가? 물론 이 null 안 쓰려면 응답 통일 형식부터 갈아엎어야 하는 대공사가 필요하긴 함
    }

    @PostMapping("/password/initialize")
    @Operation(
            summary = "비밀번호 초기화 API",
            description = "비밀번호를 변경하는 API입니다. 사용자가 비밀번호를 잊었을 때 사용합니다."
    )
    public ApiResponse<Void> passwordInitialize(@RequestParam("email") @NotBlank @Email String email) {
        credentialQueryService.initializePasswordByEmail(email);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @PostMapping("/email/exist")
    @Operation(
            summary = "이메일 존재 여부 확인 API",
            description = "이메일 존재 여부를 확인하는 API입니다. 회원가입 시 중복 여부나 로그인 시 메일 존재 여부 확인 등의 용도로 사용할 수 있습니다."
    )
    public ApiResponse<MemberResponseDTO.EmailExistResultDTO> emailExist(@RequestParam("email") @NotBlank @Email String email) {
        //항상 DTO 쓰다 여기에만 RequestParam 쓰니 이상하긴 하네...
        //아 그냥 ApiResponse<Boolean> 반환해버리고 싶다
        boolean emailExist = memberCommandService.isEmailExist(email);
        return ApiResponse.onSuccess(new MemberResponseDTO.EmailExistResultDTO(emailExist));
    }
}