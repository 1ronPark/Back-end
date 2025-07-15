package umc.lightup.position.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.lightup.api.ApiResponse;
import umc.lightup.position.converter.PositionConverter;
import umc.lightup.position.dto.PositionResponseDTO;
import umc.lightup.position.service.PositionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/positions")
public class PositionRestController {

    private final PositionService positionService;

    @GetMapping
    public ApiResponse<PositionResponseDTO.positionListDTO> getPositions() {
        List<String> positionsList = positionService.getPositionsList();
        return ApiResponse.onSuccess(PositionConverter.toPositionListDTO(positionsList));
    }
}
