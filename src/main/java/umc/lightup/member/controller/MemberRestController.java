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
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.service.CredentialQueryService;
import umc.lightup.member.service.MemberCommandService;

import java.util.List;

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
    @Operation(summary = "포지션 선택 API", description = "유저가 포지션을 선택하는 API 입니다.")
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
    @Operation(summary = "포지션 취소 API", description = "유저가 포지션 선택을 취소할 때 호출되는 API 입니다.")
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

    @GetMapping("/me/profile")
    @Operation(
            summary = "회원 프로필 조회 API",
            description = "자신의 회원 프로필을 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MyProfileDTO> getMemberProfile(
            Authentication authentication) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        return ApiResponse.onSuccess(memberCommandService.getMemberProfile(member));
    }

    @PutMapping("/me/profile")
    @Operation(
            summary = "회원 프로필 변경 API",
            description = "회원 프로필을 변경하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MyProfileDTO> changeMemberProfile(
            Authentication authentication,
            @RequestBody @Valid MemberRequestDTO.ProfileChangeDto request) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        if (request.getActivities() == null) request.setActivities(List.of()); //nullable하니 오류 방지를 위함
        return ApiResponse.onSuccess(memberCommandService.putMemberProfile(member, request));
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
    public ApiResponse<Void> passwordInitialize(@RequestParam @NotBlank @Email String email) {
        credentialQueryService.initializePasswordByEmail(email);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
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

    @PostMapping("/{memberId}/like")
    @Operation(
            summary = "회원 좋아요 등록 API",
            description = "회원이 다른 회원에게 좋아요를 등록하는 API입니다.",
            security = {@SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<Void> addMemberLike(Authentication authentication,
                                           @PathVariable("memberId") long memberId) {
        //귀찮았을 뿐 사실 여기에서 존재하는 memberId인지 확인까지 하는 게 맞음, 다만 현재 코드도 Service단에서 확인을 진행해서 동작은 정상적
        //근데 왜 이메일 확인은 자연스럽게 아무데서도 안 하고 있는 거지?
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        memberCommandService.addMemberLike(member, memberId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @DeleteMapping("/{memberId}/like")
    @Operation(
            summary = "회원 좋아요 등록 취소 API",
            description = "회원이 다른 회원에게 진행한 좋아요 등록을 취소하는 API입니다.",
            security = {@SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<Void> removeMemberLike(Authentication authentication,
                                              @PathVariable("memberId") long memberId) {
        //귀찮았을 뿐 사실 여기에서 존재하는 memberId인지 확인까지 하는 게 맞음
        String email = authentication.getName();
        // Member member = memberCommandService.getMember(email);
        memberCommandService.removeMemberLike(email, memberId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
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


    @PostMapping("/regions")
    @Operation(summary = "유저 선호 지역 선택 API", description = "유저가 선호 지역을 선택하는 API입니다.")
    public ApiResponse<MemberResponseDTO.selectRegionResultsDTO> selectRegions(Authentication authentication, @RequestBody @Valid MemberRequestDTO.MemberRegionListRequestDTO request) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        List<MemberResponseDTO.singleRegionResultDTO> resultDTOList = memberCommandService.selectRegions(member, request);
        return ApiResponse.onSuccess(MemberConverter.toSelectRegionResultsDTO(resultDTOList));
    }
}