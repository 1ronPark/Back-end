package umc.lightup.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.aws.s3.AmazonS3Manager;
import umc.lightup.common.Uuid;
import umc.lightup.common.repository.UuidRepository;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.item.converter.ItemConverter;
import umc.lightup.item.domain.*;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.enums.CategoryType;
import umc.lightup.item.enums.ItemApplyStatus;
import umc.lightup.item.repository.*;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRegionRepository;
import umc.lightup.position.domain.Position;
import umc.lightup.position.repository.PositionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCommandServiceImpl implements ItemCommandService {

    private final ItemRepository itemRepository;
    private final MemberRegionRepository memberRegionRepository;
    private final RecruitPositionRepository recruitPositionRepository;
    private final PositionRepository positionRepository;
    private final ItemLikeRepository itemLikeRepository;
    private final ItemViewHistoryRepository itemViewHistoryRepository;
    private final ItemApplyRepository itemApplyRepository;
    private final ItemCommentRepository itemCommentRepository;

    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    @Override
    @Transactional
    public Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile itemProfileImage, MultipartFile itemPlanFile) {
        Item item = ItemConverter.toItem(request, member);

        if (itemProfileImage != null) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            String itemProfileImageUrl = s3Manager.uploadFile(s3Manager.generateItemProfileImageKeyName(savedUuid), itemProfileImage);
            item.uploadItemProfile(itemProfileImageUrl);
        }

        if (itemPlanFile != null) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            String itemPlanFileUrl = s3Manager.uploadFile(s3Manager.generateItemFileKeyName(savedUuid), itemPlanFile);
            item.uploadItemPlanFile(itemPlanFileUrl);
        }

        for (ItemRequestDTO.ItemCategoryRequestDTO dto : request.getItemCategories()) {
            CategoryType categoryType = CategoryType.toCategoryType(dto.getItemCategory());
            ItemCategory itemCategory = ItemCategory.builder()
                    .item(item)
                    .categoryType(categoryType)
                    .build();

            item.addItemCategory(itemCategory);
        }

        for (ItemRequestDTO.CollaborationRegionRequestDTO dto : request.getCollaborationRegions()) {
            ItemRegion itemRegion = ItemRegion.builder()
                    .item(item)
                    .siDo(dto.getSiDo())
                    .siGunGu(dto.getSiGunGu() == null ? "전체" : dto.getSiGunGu())
                    .build();

            item.addItemRegion(itemRegion);
        }

        for (ItemRequestDTO.RecruitPositionRequestDTO dto : request.getRecruitPositions()) {
            Long positionId = dto.getPositionId();
            Position position = positionRepository.findById(positionId)
                    .orElseThrow(() -> new GeneralHandler(ErrorStatus.POSITION_NOT_FOUND));

            RecruitPosition recruitPosition = RecruitPosition.builder()
                    .item(item)
                    .position(position)
                    .mainTasks(dto.getMainTasks())
                    .preferentialTreatment(dto.getPreferentialTreatment())
                    .preferMbti(dto.getPreferMbti())
                    .recruitNumber(dto.getRecruitNumber())
                    .build();

            item.addRecruitPosition(recruitPosition);
        }

        return itemRepository.save(item);
    }

    @Override
    public Set<Long> findItemLikes(long memberId) {
        return itemLikeRepository.findItemIdsLikedByMemberId(memberId);
    }

    @Override
    public List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable, Set<Long> likedItemIds) {
        Page<Item> itemPage = itemRepository.findAll(pageable);

        return itemPage.stream()
                .map(item -> {
                    String itemImageUrl = item.getItemProfileImageUrl();
                    boolean liked = likedItemIds != null && likedItemIds.contains(item.getId());
                    int commentCount = itemCommentRepository.countByItemId(item.getId());

                    return ItemConverter.toItemResultDTO(item, itemImageUrl, commentCount, liked);
                }).toList();
    }

    @Override
    public List<ItemResponseDTO.MyItemResultDTO> getMyItems(Member member) {
        List<Item> myItemList = itemRepository.findByMember(member);

        return myItemList.stream()
            .map(item -> {
                String itemImageUrl = item.getItemProfileImageUrl();

                return ItemConverter.toMyItemResultDTO(item, itemImageUrl);
            })
            .toList();
    }

    @Override
    public Item getSingleItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));
    }

    @Override
    public Item getSingleItemWithComments(Long itemId) {
        return itemRepository.findByIdWithCommentsAndCommentMembers(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));
    }

    @Override
    public boolean getItemLike(long memberId, long itemId) {
        return itemLikeRepository.existsByMemberIdAndItemId(memberId, itemId);
    }

    @Override
    public List<ItemResponseDTO.ItemRegionResultDTO> getItemRegions(Item item) {
        Member member = item.getMember();

        return memberRegionRepository.findByMember(member).stream()
                .map(ItemConverter::toItemRegionResultDTO)
                .toList();
    }

    @Override
    public List<ItemResponseDTO.RecruitPositionResultDTO> getItemRecruitPositions(Item item) {
        return recruitPositionRepository.findByItem(item).stream()
                .map(ItemConverter::toRecruitPositionResultDTO)
                .toList();
    }

    @Override
    @Transactional
    public void addItemLike(Member member, long itemId) {
        Item findItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));

        if (findItem.getMember().equals(member)) {
            throw new GeneralHandler(ErrorStatus.MY_ITEM_LIKE);
        }

        if (itemLikeRepository.existsByMemberIdAndItemId(member.getId(), itemId)) {
            throw new GeneralHandler(ErrorStatus.ITEM_ALREADY_LIKED);
        }

        itemLikeRepository.save(ItemLike.builder()
                .member(member)
                .item(findItem)
                .build());
    }

    @Override
    @Transactional
    public void removeItemLike(String email, long itemId) {
        if (itemLikeRepository.removeByMemberEmailAndItemId(email, itemId) == 0) {
            throw new GeneralHandler(ErrorStatus.ITEM_LIKE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void updateItemHistory(Member member, Item item) {
        Optional<ItemViewHistory> optionalHistory = itemViewHistoryRepository.findByMemberAndItem(member, item);

        if (optionalHistory.isPresent()) {
            ItemViewHistory itemViewHistory = optionalHistory.get();
            itemViewHistory.updateViewedAt();
        } else {
            itemViewHistoryRepository.save(ItemViewHistory.builder()
                    .member(member)
                    .item(item)
                    .viewedAt(LocalDateTime.now())
                    .build());
        }

        if (itemRepository.increaseViewCount(item.getId()) == 0) {
            throw new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public ItemApply applyItem(Member member, Item item) {
        if (itemApplyRepository.existsByMemberAndItem(member, item)) {
            throw new GeneralHandler(ErrorStatus.DUPLICATE_ITEM_APPLY);
        }

        return itemApplyRepository.save(ItemApply.builder()
                .member(member)
                .item(item)
                .status(ItemApplyStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public ItemComment createItemComment(Member member, Long itemId, ItemRequestDTO.ItemCommentRequestDTO request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));

        ItemComment createdItemComment = ItemComment.builder()
                .content(request.getContent())
                .commentMember(member)
                .item(item)
                .build();

        return itemCommentRepository.save(createdItemComment);
    }

    @Override
    @Transactional
    public void removeItemComment(Member member, Long commentId) {
        if (itemCommentRepository.deleteByCommentMemberAndId(member, commentId) == 0) {
            throw new GeneralHandler(ErrorStatus.ITEM_COMMENT_NOT_FOUND);
        }
    }

    @Override
    public List<ItemResponseDTO.ItemCommentResultDTO> getItemComments(Item item) {
        return item.getItemComments().stream()
                .map(ItemConverter::toItemCommentResultDTO)
                .toList();
    }

    @Override
    public int countComments(Long itemId) {
        return itemCommentRepository.countByItemId(itemId);
    }

    @Override
    public List<ItemResponseDTO.ItemCategoriesResultDTO> getItemCategories(Item item) {
        return itemCategoryRepository.findByItem(item).stream()
                .map(ItemConverter::toItemCategoriesResultDTO)
                .toList();
    }
}
