package umc.lightup.item.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.item.domain.Item;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

import java.util.List;

public interface ItemCommandService {
    Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile file);

    List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable);

    List<ItemResponseDTO.MyItemResultDTO> getMyItems(Member member);
}
