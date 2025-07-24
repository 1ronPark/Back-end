package umc.lightup.region.converter;

import umc.lightup.region.dto.RegionResponseDTO;

import java.util.List;

public class RegionConverter {

    public static RegionResponseDTO.SiDoListDTO toSiDoResultDTO(List<String> siDoList) {
        return RegionResponseDTO.SiDoListDTO.builder()
                .siDo(siDoList)
                .build();
    }

    public static RegionResponseDTO.SiGunGuListDTO toSiGunGuResultDTO(List<String> siGunGuList) {
        return RegionResponseDTO.SiGunGuListDTO.builder()
                .siGunGu(siGunGuList)
                .build();
    }
}
