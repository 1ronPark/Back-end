package umc.lightup.region.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.lightup.api.ApiResponse;
import umc.lightup.region.converter.RegionConverter;
import umc.lightup.region.dto.RegionReponseDTO;
import umc.lightup.region.service.RegionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/regions")
public class RegionRestController {

    private final RegionService regionService;

    @GetMapping("/sido")
    @Operation(summary = "시/도 단위 지역 조회 API", description = "시/도 단위로 지역을 조회할 수 있는 API 입니다.")
    public ApiResponse<RegionReponseDTO.SiDoListDTO> getSiDo() {
        List<String> siDoList = regionService.getSiDoList();
        return ApiResponse.onSuccess(RegionConverter.toSiDoResultDTO(siDoList));
    }

    @GetMapping("/sigungu")
    @Operation(summary = "시/군/구 단위 지역 조회 API",
            description = "시/군/구 단위로 지역을 조회할 수 있는 API 입니다. 요청 파라미터로 시/도 값을 받아 해당 시/도 하위의 시/군/구를 조회할 수 있습니다.")
    public ApiResponse<RegionReponseDTO.SiGunGuListDTO> getSiGunGu(@RequestParam String sido) {
        List<String> siGunGuList = regionService.getSiGunGuList(sido);
        return ApiResponse.onSuccess(RegionConverter.toSiGunGuResultDTO(siGunGuList));
    }
}
