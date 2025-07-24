package umc.lightup.region.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.lightup.region.domain.Region;
import umc.lightup.region.repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<String> getSiDoList() {
        return regionRepository.findDistinctSiDo();
    }

    public List<String> getSiGunGuList(String siDo) {
        List<Region> regionList = regionRepository.findBySiDo(siDo);
        return regionList.stream()
                .map(Region::getSiGunGu)
                .distinct()
                .toList();
    }

    public boolean isSiDoExist(String value) {
        return regionRepository.existsBySiDo(value);
    }

    public boolean isSiGunGuExist(String value) {
        return regionRepository.existsBySiGunGu(value);
    }
}
