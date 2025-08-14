package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.service.MemberCommandService;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members") // 사실 이것도 같이 변경하는 게 맞으나 프론트에서 이미 작업이 이루어져 어려울듯...
public class MemberCommonRestController {
    private final MemberCommandService memberCommandService;

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

    @GetMapping("/{memberId}")
    @Operation(
            summary = "회원(타인) 정보 조회 API",
            description = "타인의 회원 정보를 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> getMemberInfo(Authentication authentication,
                                                                      @PathVariable("memberId") long id) {
        Member member = null;
        if (authentication != null) {
            String email = authentication.getName();
            member = memberCommandService.getMember(email);
        }
        return ApiResponse.onSuccess(memberCommandService.getMember(id, member));
    }

    @GetMapping("/search")
    @Operation(
            summary = "회원 검색 API",
            description = "회원 프로필을 검색하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MemberInfoListDTO> searchMember(
            Authentication authentication,
            @RequestParam(value = "positions", required = false) List<String> positions,
            @RequestParam(value = "mbtiE", required = false) Boolean mbtiE,
            @RequestParam(value = "mbtiN", required = false) Boolean mbtiN,
            @RequestParam(value = "mbtiF", required = false) Boolean mbtiF,
            @RequestParam(value = "mbtiP", required = false) Boolean mbtiP,
            // DTO로 받을 수 없음(Spring에서 파싱 불가), 따라서 직접 파싱
            @RequestParam(value = "regions", required = false) List<String> regions,
            @RequestParam(value = "onlyLiked", required = false) Boolean onlyLiked,
            @RequestParam(value = "page", required = false) @Positive Long page,
            @RequestParam(value = "limit", required = false) @Positive Long limit) {
        Member member = null;
        if (authentication != null) {
            String email = authentication.getName();
            member = memberCommandService.getMember(email);
        }
        MemberRequestDTO.MemberSearchRequestDTO request = MemberRequestDTO.MemberSearchRequestDTO.builder()
                .positions(positions)
                .mbtiE(mbtiE)
                .mbtiN(mbtiN)
                .mbtiF(mbtiF)
                .mbtiP(mbtiP)
                .regions(regions==null?null:regions.stream()
                        .map(r-> {
                            String[] split = r.split("\\s+", 2);
                            if (split.length == 0) return null;
                            else return MemberRequestDTO.MemberRegionRequestDTO.builder()
                                        .siDo(split[0])
                                        .siGunGu(split.length == 1 ? null : split[1])
                                        .build();
                        })
                        .filter(Objects::nonNull)
                        .toList())
                .onlyLiked(onlyLiked)
                .page(page)
                .limit(limit)
                .build();
        return ApiResponse.onSuccess(memberCommandService.searchMember(member, request));
    }


    @GetMapping("/recent")
    @Operation(
            summary = "최근 조회한 회원 조회 API",
            description = "최근 조회한 회원 내역을 조회하는 API입니다. 로그인이 필수입니다.",
            security = {@SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MemberHistoryInfoListDTO> searchHistory(
            Authentication authentication,
            @RequestParam(value = "size", defaultValue = "5") @Positive Long size) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        return ApiResponse.onSuccess(
                MemberConverter.toMemberHistoryInfoListDTO(
                        memberCommandService.getHistory(member, size)));
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
}