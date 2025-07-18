package umc.lightup.position.converter;

import umc.lightup.position.dto.PositionResponseDTO;

import java.util.List;

public class PositionConverter {

    public static PositionResponseDTO.positionListDTO toPositionListDTO(List<String> positions) {
        return PositionResponseDTO.positionListDTO.builder()
                .positions(positions)
                .build();
    }
}
