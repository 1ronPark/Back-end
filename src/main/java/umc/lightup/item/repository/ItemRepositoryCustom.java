package umc.lightup.item.repository;

import org.springframework.data.domain.Pageable;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

import java.util.List;

public interface ItemRepositoryCustom {
    List<ItemResponseDTO.ItemResultDTO> searchItems(Member requestedMember,
                                                    Pageable pageable,
                                                    String category,
                                                    Long positionId,
                                                    ItemRequestDTO.ItemRegionSearchRequestDTO itemRegionDTOs,
                                                    Boolean onlyLiked,
                                                    String sort);
}
