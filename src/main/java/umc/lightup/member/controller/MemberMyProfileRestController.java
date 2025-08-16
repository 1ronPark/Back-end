package umc.lightup.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.Portfolio;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.dto.PortfolioInfoDTO;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.member.validation.annotation.ImageFile;
import umc.lightup.member.validation.annotation.ReadableDocumentFile;

import java.util.List;

import static umc.lightup.member.dto.MemberResponseDTO.memberPositionDeleteResultDTOBuilder;
import static umc.lightup.member.dto.MemberResponseDTO.memberPositionResultDTOBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members") // 사실 이것도 같이 변경하는 게 맞으나 프론트에서 이미 작업이 이루어져 어려울듯...
public class MemberMyProfileRestController {
    private final MemberCommandService memberCommandService;

    @PostMapping("/position")
    @Operation(summary = "포지션 선택 API", description = "유저가 포지션을 선택하는 API 입니다.")
    public ApiResponse<MemberResponseDTO.MemberPositionResultDTO> selectMemberPosition(Authentication authentication, @RequestParam("positionName") String positionName) {
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
    public ApiResponse<MemberResponseDTO.MemberPositionDeleteResultDTO> deleteMemberPosition(Authentication authentication, @RequestParam("positionName") String positionName) {
        String userName = authentication.getName();
        Member member = memberCommandService.getMember(userName);

        memberCommandService.deletePosition(member.getId(), positionName);

        return ApiResponse.onSuccess(memberPositionDeleteResultDTOBuilder()
                .memberName(userName)
                .deletePositionName(positionName)
                .build());
    }

    @GetMapping("/me/profile")
    @Operation(
            summary = "회원 프로필 조회 API",
            description = "자신의 회원 프로필을 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.MyProfileDTO> getMemberProfile(
            Authentication authentication) {
        return ApiResponse.onSuccess(memberCommandService.getMemberProfile(authentication.getName()));
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
        if (request.getActivities() == null) request.setActivities(List.of()); //nullable하니 오류 방지를 위함
        return ApiResponse.onSuccess(memberCommandService.putMemberProfile(authentication.getName(), request));
    }

    @PostMapping(value = "/me/profile/image/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "회원 프로필 사진 변경 API",
            description = "회원 프로필 사진을 변경하는 API입니다. 프로필 사진 업로드가 필요합니다. 사진 파일만 업로드가 가능합니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.ProfileImageSaveResultDTO> changeMemberImage(
            Authentication authentication,
            @RequestPart(value = "profileImage") @NotNull @ImageFile MultipartFile profileImage) {
        Member member = memberCommandService.getMember(authentication.getName());
        String profileImageUrl = memberCommandService.saveMemberProfileImage(member, profileImage);
        return ApiResponse.onSuccess(
                MemberResponseDTO.ProfileImageSaveResultDTO.builder()
                        .profileImageUrl(profileImageUrl)
                        .build());
    }

    @PostMapping(value = "/me/profile/portfolio/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "회원 프로필의 포트폴리오 등록 API",
            description = "회원 프로필의 포트폴리오를 파일로 등록하는 API입니다. 포트폴리오 파일 업로드가 필요합니다. 이름은 최대 30자까지만 입력할 수 있습니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.PortfolioInfoWithIdDTO> portFolioFileRegister(
            Authentication authentication,
            // 아니 이걸 DTO로 안 받고 바로 String으로 받으면 에러나네...
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") @Valid MemberRequestDTO.PortFolioNameRequestDTO request,
            @RequestPart(value = "portfolioFile") @NotNull @ReadableDocumentFile MultipartFile portfolioFile) {
        Member member = memberCommandService.getMember(authentication.getName());
        Portfolio saved = memberCommandService.savePortfolio(member, request.getName(), portfolioFile);
        return ApiResponse.onSuccess(MemberConverter.toPortfolioInfoWithIdDTO(saved));
    }

    @PostMapping(value = "/me/profile/portfolio/link")
    @Operation(
            summary = "회원 프로필의 포트폴리오 등록 API",
            description = "회원 프로필의 포트폴리오를 링크로 등록하는 API입니다. 유효한 링크인지는 확인하지 않습니다. 이름은 최대 30자까지만 입력할 수 있습니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.PortfolioInfoWithIdDTO> portFolioLinkRegister(
            Authentication authentication,
            @RequestBody @Valid PortfolioInfoDTO portfolioInfo) {
        Member member = memberCommandService.getMember(authentication.getName());
        Portfolio saved = memberCommandService.savePortfolio(member,
                portfolioInfo.getName(),
                portfolioInfo.getFileUrl());
        return ApiResponse.onSuccess(MemberConverter.toPortfolioInfoWithIdDTO(saved));
    }

    @DeleteMapping(value = "/me/profile/portfolio")
    @Operation(
            summary = "회원 프로필의 포트폴리오 삭제 API",
            description = "회원 프로필의 포트폴리오를 삭제하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN")}
    )
    public ApiResponse<MemberResponseDTO.ProfileImageSaveResultDTO> removePortFolio(
            Authentication authentication,
            @RequestBody @Valid MemberRequestDTO.PortFolioRequestDTO portfolioRequestDTO) {
        String email = authentication.getName();
        memberCommandService.removePortfolio(email, portfolioRequestDTO.getPortfolioId());
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

    @DeleteMapping("/skills/{skillId}")
    @Operation(
            summary = "스킬 선택 취소 API",
            description = "유저가 스킬 선택을 취소하는 API입니다. 스킬은 하나씩 취소할 수 있습니다."
    )
    public ApiResponse<Void> deleteSkill(Authentication authentication, @PathVariable("skillId") Long skillId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        memberCommandService.removeMemberSkill(skillId, member.getId());
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
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


    @DeleteMapping("/strengths/{strengthId}")
    @Operation(
            summary = "강점 선택 취소 API",
            description = "유저가 강점 선택을 취소하는 API입니다. 강점은 하나씩 취소할 수 있습니다."
    )
    public ApiResponse<Void> deleteStrength(Authentication authentication, @PathVariable("strengthId") Long strengthId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        memberCommandService.removeMemberStrength(strengthId, member.getId());
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }


    @PostMapping("/regions")
    @Operation(summary = "유저 선호 지역 선택 API", description = "유저가 선호 지역을 선택하는 API입니다.")
    public ApiResponse<MemberResponseDTO.selectRegionResultsDTO> selectRegions(Authentication authentication, @RequestBody @Valid MemberRequestDTO.MemberRegionListRequestDTO request) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        List<MemberResponseDTO.singleRegionResultDTO> resultDTOList = memberCommandService.selectRegions(member, request);
        return ApiResponse.onSuccess(MemberConverter.toSelectRegionResultsDTO(resultDTOList));
    }

    @DeleteMapping("/regions/{memberRegionId}")
    @Operation(summary = "유저 선호 지역 취소 API", description = "유저가 선호 지역을 취소하는 API입니다. 선호 지역 취소는 하나씩 가능합니다.")
    public ApiResponse<Void> deleteRegion(Authentication authentication, @PathVariable("memberRegionId") Long memberRegionId) {

        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        memberCommandService.removeMemberRegion(memberRegionId, member.getId());
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}