package umc.lightup.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.lightup.api.ApiResponse;
import umc.lightup.school.converter.SchoolConverter;
import umc.lightup.school.domain.School;
import umc.lightup.school.dto.SchoolResponseDTO;
import umc.lightup.school.service.SchoolQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/school")
public class SchoolRestController {

  private final SchoolQueryService schoolQueryService;

  @GetMapping
  @Operation(
          summary = "대학 목록 불러오기 API",
          description = "대학 목록 조회를 위한 API 이며 페이징을 포함합니다,검색값이 없을 경우 가나다 순서대로 불러와집니다. 이름을 검색하여 가져올 수 있습니다.",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<SchoolResponseDTO.SchoolListDTO> getSchoolList(
          Authentication authentication,
          @RequestParam(name = "keyword", required = false) String keyword,
          @Min(0) @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
          @Min(1) @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){
    Page<School> schoolList = schoolQueryService.getSchoolList(keyword, page, size);
    return ApiResponse.onSuccess(SchoolConverter.schoolListDTO(schoolList));
  }
}
