package umc.lightup.region.converter;

import umc.lightup.region.dto.RegionReponseDTO;

import java.util.List;

public class RegionConverter {

    public static RegionReponseDTO.SiDoListDTO toSiDoResultDTO(List<String> sidoList) {
        return RegionReponseDTO.SiDoListDTO.builder()
                .sido(sidoList)
                .build();
    }

    public static RegionReponseDTO.SiGunGuListDTO toSiGunGuResultDTO(List<String> sigunguList) {
        return RegionReponseDTO.SiGunGuListDTO.builder()
                .sigungu(sigunguList)
                .build();
    }
}
