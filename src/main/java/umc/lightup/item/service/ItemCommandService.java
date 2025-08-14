package umc.lightup.item.service;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemApply;
import umc.lightup.item.domain.ItemComment;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemCommandService {
    Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile itemProfileImage, MultipartFile itemPlanFile);
    Item changeItem(Member member, Long itemId, ItemRequestDTO.ItemChangeRequestDTO request, MultipartFile itemProfileImage, MultipartFile itemPlanFile);
    Item getSingleItem(Long itemId);
    Item getSingleItemWithComments(Long itemId);
    ItemApply applyItem(Member member, Item item);
    boolean getItemApplyStatus(Member member, Item item);
    boolean getItemLike(long memberId, long itemId);
    void removeItem(Member member, Long itemId);
    void addItemLike(Member member, long itemId);
    void removeItemLike(String email, long itemId);
    void updateItemHistory(Member member, Item item);
    ItemComment createItemComment(Member member, Long itemId, ItemRequestDTO.ItemCommentRequestDTO request);
    void removeItemComment(Member member, Long commentId);
    Set<Long> findItemLikes(long memberId);
    int countComments(Long itemId);

    List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable, @Nullable Set<Long> likedItemIds);

    List<ItemResponseDTO.MyItemResultDTO> getMyItems(Member member);

    List<ItemResponseDTO.ItemRegionResultDTO> getItemRegions(Item item);

    List<ItemResponseDTO.RecruitPositionResultDTO> getItemRecruitPositions(Item item);

    List<ItemResponseDTO.ItemCategoriesResultDTO> getItemCategories(Item item);

    List<ItemResponseDTO.ItemCommentResultDTO> getItemComments(Item item);
}
