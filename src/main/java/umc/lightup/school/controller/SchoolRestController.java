package umc.lightup.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.school.converter.SchoolConverter;
import umc.lightup.school.domain.School;
import umc.lightup.school.dto.SchoolResponseDTO;
import umc.lightup.school.service.SchoolQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/school")
public class SchoolRestController {

  private final MemberCommandService memberCommandService;
  private final SchoolQueryService schoolQueryService;

  @GetMapping
  @Operation(
          summary = "대학 목록 불러오기 API",
          description = "대학 목록 조회를 위한 API 이며 페이징을 포함합니다. 키워드가 없을 경우 가나다 순서대로 모든목록이 불러와 집니다. 이름을 검색하여 가져올 수 있습니다.",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<SchoolResponseDTO.SchoolListDTO> getSchoolList(
          @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9\\s]*$", message = "검색어에는 특수문자를 사용할 수 없습니다.")
          @RequestParam(name = "keyword", required = false) String keyword,
          @Min(0) @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
          @Min(1) @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){
    Page<School> schoolList = schoolQueryService.getSchoolList(keyword, page, size);
    return ApiResponse.onSuccess(SchoolConverter.schoolListDTO(schoolList));
  }

  @PostMapping("/sendEmail")
  @Operation(
          summary = "대학 이메일 인증메일 발송 API",
          description = "대학 이메일 인증메일 발송을 위한 API 입니다",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<Void> sendSchoolEmail(
          Authentication authentication,
          @RequestParam @NotNull Long schoolId,
          @RequestParam @NotBlank @Email String email){
    Member member = memberCommandService.getMember(authentication.getName());
    schoolQueryService.sendEmailVerification(member, schoolId, email);
    return ApiResponse.of(SuccessStatus._SEND_COMPLETE, null);
  }

  @PostMapping("/verifyEmail")
  @Operation(
          summary = "대학 이메일 인증 API",
          description = "대학 이메일 인증을 위한 API 입니다. 학교 이메일과 인증코드를 보내주세요",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<Void> verifyEmail(
          @RequestParam @NotBlank @Email String email,
          @RequestParam @NotNull String code
  ) {
    schoolQueryService.verifyEmail(email, code);
    return ApiResponse.of(SuccessStatus._OK, null);
  }
}
