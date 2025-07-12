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
        return regionRepository.findDistinctSido();
    }

    public List<String> getSiGunGuList(String sido) {
        List<Region> regionList = regionRepository.findBySido(sido);
        return regionList.stream()
                .map(Region::getSigungu)
                .distinct()
                .toList();
    }
}
