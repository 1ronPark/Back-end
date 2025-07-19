package umc.lightup.position.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.lightup.position.domain.Position;
import umc.lightup.position.repository.PositionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    public List<String> getPositionsList() {
        List<Position> findPositions = positionRepository.findAll();
        return findPositions.stream()
                .map(Position::getName)
                .toList();
    }
}
