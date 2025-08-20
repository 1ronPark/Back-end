package umc.lightup.item.repository;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import umc.lightup.item.dto.ItemRequestDTO;

public interface ItemRepositoryCustom {

    Page<Tuple> searchItems(Pageable pageable, String category, Long positionId, ItemRequestDTO.ItemRegionSearchRequestDTO itemRegionDTOs, String sort);
}
