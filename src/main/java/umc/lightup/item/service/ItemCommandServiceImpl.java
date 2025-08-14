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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            uploadItemProfileImageToS3(itemProfileImage, item);
        }

        if (itemPlanFile != null) {
            uploadItemPlanFileToS3(itemPlanFile, item);
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
    @Transactional
    public Item changeItem(Member member, Long itemId,
                           ItemRequestDTO.ItemChangeRequestDTO request,
                           MultipartFile itemProfileImage, MultipartFile itemPlanFile) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));

        if (!item.getMember().equals(member)) {
            throw new GeneralHandler(ErrorStatus.ITEM_UPDATE_FORBIDDEN);
        }

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getIntroduce() != null) {
            item.setIntroduce(request.getIntroduce());
        }
        if (request.getDescription() != null) {
        item.setDescription(request.getDescription());
        }
        item.setProjectStatus(request.getProjectStatus());
        if (request.getExtraLink1() != null) {
            item.setExtraLink1(request.getExtraLink1());
        }
        if (request.getExtraLink2() != null) {
            item.setExtraLink2(request.getExtraLink2());
        }

        if (itemProfileImage != null) {
            item.setItemProfileImageUrl(itemProfileImage.toString());
            uploadItemProfileImageToS3(itemProfileImage, item);
        }

        if (itemPlanFile != null) {
            item.setItemPlanFileUrl(itemPlanFile.toString());
            uploadItemPlanFileToS3(itemPlanFile, item);
        }

        if (request.getItemCategories() != null) {
            item.clearItemCategories();
            for (ItemRequestDTO.ItemCategoryRequestDTO dto : request.getItemCategories()) {
                CategoryType categoryType = CategoryType.toCategoryType(dto.getItemCategory());
                ItemCategory itemCategory = ItemCategory.builder()
                        .item(item)
                        .categoryType(categoryType)
                        .build();

                item.addItemCategory(itemCategory);
            }
        }

        if (request.getCollaborationRegions() != null) {
            item.clearItemRegions();
            for (ItemRequestDTO.CollaborationRegionRequestDTO dto : request.getCollaborationRegions()) {
                ItemRegion itemRegion = ItemRegion.builder()
                        .item(item)
                        .siDo(dto.getSiDo())
                        .siGunGu(dto.getSiGunGu() == null ? "전체" : dto.getSiGunGu())
                        .build();

                item.addItemRegion(itemRegion);
            }
        }

        if (request.getRecruitPositions() != null) {
            item.clearRecruitPositions();
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
        }

        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeItem(Member member, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));

        if (!item.getMember().equals(member)) {
            throw new GeneralHandler(ErrorStatus.NOT_MY_ITEM);
        }

        if (itemRepository.deleteByMemberAndId(member, itemId) == 0) {
            throw new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND);
        }
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
        //내가 만든 프로젝트 가져오기
        List<Item> myItemList = itemRepository.findByMember(member);

        //내가 지원한 프로젝트 가져오기
        List<Item> appliedItemList = itemApplyRepository.findByMemberId(member.getId()).stream()
                .map(ItemApply::getItem)
                .toList();

        //필요한 프로젝트들 모두 합치기
        List<Item> allItems = Stream.concat(myItemList.stream(), appliedItemList.stream())
                .toList();

        List<ItemCategory> itemCategories = itemCategoryRepository.findByItems(allItems);
        Map<Long, List<ItemCategory>> categoryMap = itemCategories.stream()
                .collect(Collectors.groupingBy(ic -> ic.getItem().getId()));

        return allItems.stream()
            .map(item -> {
                String itemImageUrl = item.getItemProfileImageUrl();

                List<ItemResponseDTO.ItemCategoriesResultDTO> itemCategoriesResultDTOList = categoryMap.getOrDefault(item.getId(), List.of()).stream()
                        .map(ItemConverter::toItemCategoriesResultDTO)
                        .toList();

                boolean applicantStatus = itemApplyRepository.existsByItemId(item.getId());
                boolean isMyApplyItem = !item.getMember().equals(member);

                return ItemConverter.toMyItemResultDTO(item, itemImageUrl, itemCategoriesResultDTOList, applicantStatus, isMyApplyItem);
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
    public boolean getItemApplyStatus(Member member, Item item) {
        if (!itemApplyRepository.existsByMemberAndItem(member, item)) {
            return false;
        }

        ItemApplyStatus itemApplyStatus = itemApplyRepository.findByMemberAndItem(member, item).getStatus();
        return itemApplyStatus.equals(ItemApplyStatus.PENDING);
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


    private void uploadItemPlanFileToS3(MultipartFile itemPlanFile, Item item) {
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String itemPlanFileUrl = s3Manager.uploadFile(s3Manager.generateItemFileKeyName(savedUuid), itemPlanFile);
        item.uploadItemPlanFile(itemPlanFileUrl);
    }

    private void uploadItemProfileImageToS3(MultipartFile itemProfileImage, Item item) {
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String itemProfileImageUrl = s3Manager.uploadFile(s3Manager.generateItemProfileImageKeyName(savedUuid), itemProfileImage);
        item.uploadItemProfile(itemProfileImageUrl);
    }
}
