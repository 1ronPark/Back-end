package umc.lightup.strength.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.strength.converter.StrengthConverter;
import umc.lightup.strength.dto.StrengthResponseDTO;
import umc.lightup.strength.service.StrengthService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/strengths")
public class StrengthRestController {

    private final StrengthService strengthService;

    @GetMapping
    @Operation(
            summary = "유저의 강점 조회 API",
            description = "유저가 강점을 선택하기 전에 드롭다운으로 강점들을 조회하는 API입니다."
    )
    public ApiResponse<StrengthResponseDTO.strengthListDTO> getStrengths(@RequestParam("positionName") String positionName) {
        List<String> strengthsList = strengthService.getStrengthsList(positionName);
        return ApiResponse.onSuccess(StrengthConverter.toStrengthListDTO(strengthsList));
    }

    //커스텀 강점 생성 기능 삭제
/*    @PostMapping
    @Operation(
            summary = "유저의 커스텀 강점 생성 API",
            description = "유저가 직접 강점을 생성하고 선택하는 API입니다. 유저가 생성한 강점은 생성과 동시에 선택됩니다."
    )
    public ApiResponse<StrengthResponseDTO.createdStrengthResultDTO> createCustomStrength(Authentication authentication,
                                                                                 @RequestBody StrengthRequestDTO.CreateStrengthDTO request) {
        Member member = memberCommandService.getMember(authentication.getName());
        String strengthName = strengthService.createStrength(request, member);

        return ApiResponse.onSuccess(StrengthConverter.toCreatedStrengthResultDTO(strengthName, member));
    }*/
}
