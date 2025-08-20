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
    Item getEditItem(Member member, Long itemId);
    Item getSingleItemWithComments(Long itemId);
    ItemApply applyItem(Member member, long itemId);
    boolean getItemApplyStatus(Member member, Item item);
    boolean getItemSuggestStatus(Member member, Item item);
    boolean getItemLike(long memberId, long itemId);
    void removeItem(Member member, Long itemId);
    void addItemLike(Member member, long itemId);
    void removeItemLike(String email, long itemId);
    void updateItemHistory(Member member, Item item);
    void acceptItemApply(Member itemOwner,
                         ItemRequestDTO.AcceptItemApplyRequestDTO acceptItemApplyRequestDTO);
    List<ItemResponseDTO.ItemApplyStatusDTO> getItemApplyStatus(String email);
    ItemApply offerItem(Member offeringMember, long offeredMemberId, long itemId);
    void acceptItemOffer(Member offeredMember,
                         ItemRequestDTO.AcceptItemOfferRequestDTO acceptItemOfferRequestDTO);
    ItemComment createItemComment(Member member, Long itemId, ItemRequestDTO.ItemCommentRequestDTO request);
    void removeItemComment(Member member, Long commentId);
    Set<Long> findItemLikes(long memberId);
    int countComments(Long itemId);

    List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable,
                                                    @Nullable Set<Long> likedItemIds,
                                                    @Nullable String category);

    List<ItemResponseDTO.MyItemResultDTO> getMyItems(Member member);

    List<ItemResponseDTO.ItemRegionResultDTO> getItemRegions(Item item);

    List<ItemResponseDTO.RecruitPositionResultDTO> getItemRecruitPositions(Item item);

    List<ItemResponseDTO.ItemCategoriesResultDTO> getItemCategories(Item item);

    List<ItemResponseDTO.ItemCommentResultDTO> getItemComments(Item item);

    List<ItemResponseDTO.ItemResultDTO> searchItems(Pageable pageable, @Nullable Set<Long> likedItemIds, @Nullable String category, String sort);
}
