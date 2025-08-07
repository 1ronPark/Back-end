package umc.lightup.item.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemApply;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

import java.util.List;

public interface ItemCommandService {
    Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile itemProfileImage, MultipartFile itemPlanFile);
    Item getSingleItem(Long itemId);
    ItemApply applyItem(Member member, Item item);
    void addItemLike(Member member, long itemId);
    void removeItemLike(String email, long itemId);
    void updateItemHistory(Member member, Item item);

    List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable);

    List<ItemResponseDTO.MyItemResultDTO> getMyItems(Member member);

    List<ItemResponseDTO.ItemRegionResultDTO> getItemRegions(Item item);

    List<ItemResponseDTO.RecruitPositionResultDTO> getItemRecruitPositions(Item item);
}
