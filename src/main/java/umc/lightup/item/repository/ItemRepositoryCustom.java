package umc.lightup.item.repository;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

public interface ItemRepositoryCustom {
    ItemResponseDTO.ItemInfoListDTO getItemInfos
            (Member currentMember, ItemRequestDTO.ItemSearchRequestDTO options);

    Page<Tuple> searchItems(Pageable pageable, String category, Long positionId, String sort);
}
