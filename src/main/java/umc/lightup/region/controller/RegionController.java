package umc.lightup.region.controller;

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
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/sido")
    public ApiResponse<RegionReponseDTO.SiDoListDTO> getSiDo() {
        List<String> siDoList = regionService.getSiDoList();
        return ApiResponse.onSuccess(RegionConverter.toSiDoResultDTO(siDoList));
    }

    @GetMapping("/sigungu")
    public ApiResponse<RegionReponseDTO.SiGunGuListDTO> getSiGunGu(@RequestParam String sido) {
        List<String> siGunGuList = regionService.getSiGunGuList(sido);
        return ApiResponse.onSuccess(RegionConverter.toSiGunGuResultDTO(siGunGuList));
    }
}
