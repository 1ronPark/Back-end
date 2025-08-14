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
import umc.lightup.member.repository.MemberRepository;
import umc.lightup.notification.dto.NotificationEventRequestDTO;
import umc.lightup.notification.enums.NotificationType;
import umc.lightup.notification.enums.ReferenceType;
import umc.lightup.notification.service.NotificationCommandService;
import umc.lightup.notification.utli.NotificationEventSender;
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
    private final MemberRepository memberRepository;

    private final NotificationEventSender notificationEventSender;
    private final NotificationCommandService notificationCommandService;
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
    public ItemApply applyItem(Member member, long itemId) {
        Item item = itemRepository.findByIdWithOwner(itemId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));
        if (itemApplyRepository.existsByMemberAndItem(member, item)) {
            throw new GeneralHandler(ErrorStatus.DUPLICATE_ITEM_APPLY);
        }

        //알림 전송
        NotificationEventRequestDTO.NotificationEventDTO eventDTO =
                NotificationEventRequestDTO.NotificationEventDTO.builder()
                        .senderId(member.getId())
                        .receiverId(item.getMember().getId())
                        .notificationType(NotificationType.INVITE)
                        .message(member.getNameNotNull() +
                                "님에게서 " +
                                item.getName() +
                                "에 지원했어요!")
                        .referenceType(ReferenceType.ITEM)
                        .referenceId(item.getId())
                        .build();
        notificationCommandService.saveNotification(eventDTO);
        notificationEventSender.send(eventDTO);

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

        ItemApply itemApply = itemApplyRepository.findByMemberAndItem(member, item)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_APPLY_NOT_FOUND));

        if (itemApply.isFromOwner()) {
            return false;
        }

        return itemApply.getStatus().equals(ItemApplyStatus.PENDING);
    }

    @Override
    public boolean getItemSuggestStatus(Member member, Item item) {
        if (!itemApplyRepository.existsByMemberAndItem(member, item)) {
            return false;
        }

        ItemApply itemApply = itemApplyRepository.findByMemberAndItem(member, item)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_APPLY_NOT_FOUND));

        if (!itemApply.isFromOwner()) {
            return false;
        }

        return itemApply.getStatus().equals(ItemApplyStatus.PENDING);
    }

    @Override
    @Transactional
    public void acceptItemApply(Member itemOwner,
                                ItemRequestDTO.AcceptItemApplyRequestDTO acceptItemApplyRequestDTO) {
        ItemApply itemApply = itemApplyRepository.findById(acceptItemApplyRequestDTO.getItemApplyId().longValue())
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_APPLY_NOT_FOUND));
        if (!itemOwner.equals(itemApply.getItem().getMember())) //owner가 맞는지 확인
            throw new GeneralHandler(ErrorStatus.NOT_MY_ITEM);
        if (itemApply.isFromOwner()) //제안이 아닌 지원인지 확인
            throw new GeneralHandler(ErrorStatus.NOT_APPLIED);
        if (itemApply.getStatus() != ItemApplyStatus.PENDING) //이미 수락 여부를 결정했는지 확인
            throw new GeneralHandler(ErrorStatus.ALREADY_CHOSE_OFFER_ACCEPTANCE);

        if (acceptItemApplyRequestDTO.getAccept()) itemApply.setStatus(ItemApplyStatus.ACCEPTED);
        else itemApply.setStatus(ItemApplyStatus.REJECTED);

        //알림 전송
        NotificationEventRequestDTO.NotificationEventDTO eventDTO =
                NotificationEventRequestDTO.NotificationEventDTO.builder()
                        .senderId(itemOwner.getId())
                        .receiverId(itemApply.getMember().getId())
                        .notificationType(NotificationType.INVITE)
                        .message(itemOwner.getNameNotNull() +
                                        "님이 " +
                                        itemApply.getItem().getName() +
                                        "의 지원에 대해 응답했어요! 응답을 확인해보세요!"
                                // 이렇게 보내는 게 좋은가? 다른 좋은 Message 없나?
                                // 그렇다고 알림에서부터 수락/거절 사실을 통보하면 마음아플 것 같음...
                                // 취업준비할 때 거절은 이를 돌려 말하는 것처럼...
                        )
                        .referenceType(ReferenceType.ITEM)
                        .referenceId(itemApply.getItem().getId())
                        .build();
        notificationCommandService.saveNotification(eventDTO);
        notificationEventSender.send(eventDTO);
    }

    @Override
    public List<ItemResponseDTO.ItemApplyStatusDTO> getItemApplyStatus(String email) {
        return itemApplyRepository.findAllByMemberEmail(email)
                .stream().map(i -> ItemResponseDTO.ItemApplyStatusDTO.builder()
                        .itemOwned(i.getItem().getMember() != null && i.getItem().getMember().getEmail().equals(email))
                        .itemId(i.getItem().getId())
                        .itemName(i.getItem().getName())
                        .itemImageUrl(i.getItem().getItemProfileImageUrl())
                        .itemOwnerUsername(i.getItem().getMember() == null ? null : i.getItem().getMember().getNameNotNull())
                        .memberId(i.getMember() == null ? null : i.getMember().getId())
                        .memberUsername(i.getMember() == null ? null : i.getMember().getNameNotNull())
                        .memberProfileImageUrl(i.getMember() == null ? null : i.getMember().getProfileImageUrl())
                        .applyId(i.getId())
                        .fromOwner(i.isFromOwner())
                        .applyStatus(i.getStatus())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public ItemApply offerItem(Member offeringMember, long offeredMemberId, long itemId) {
        if (offeringMember.getId().equals(offeredMemberId))
            throw new GeneralHandler(ErrorStatus.SELF_ITEM_APPLY);
        if (!itemRepository.existsByIdAndMember(itemId, offeringMember))
            throw new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND);
        Member offeredMember = memberRepository.findById(offeredMemberId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Item item = itemRepository.findById(itemId) //Comment와 fetch join할 이유 없음
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_NOT_FOUND));
        if (itemApplyRepository.existsByMemberAndItem(offeredMember, item))
            throw new GeneralHandler(ErrorStatus.DUPLICATE_ITEM_APPLY);

        //알림 전송
        NotificationEventRequestDTO.NotificationEventDTO eventDTO =
                NotificationEventRequestDTO.NotificationEventDTO.builder()
                        .senderId(offeringMember.getId())
                        .receiverId(offeredMember.getId())
                        .notificationType(NotificationType.INVITE)
                        .message(offeringMember.getNameNotNull() +
                                "님에게서 " +
                                item.getName() +
                                "의 제안이 도착했어요!")
                        .referenceType(ReferenceType.ITEM)
                        .referenceId(item.getId())
                        .build();
        notificationCommandService.saveNotification(eventDTO);
        notificationEventSender.send(eventDTO);

        return itemApplyRepository.save(ItemApply.builder()
                .member(offeredMember)
                .item(item)
                .fromOwner(true)
                .status(ItemApplyStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void acceptItemOffer(Member offeredMember, ItemRequestDTO.AcceptItemOfferRequestDTO acceptItemOfferRequestDTO) {
        ItemApply itemApply = itemApplyRepository.findById(acceptItemOfferRequestDTO.getItemApplyId().longValue())
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_APPLY_NOT_FOUND));
        if (!itemApply.isFromOwner() || !offeredMember.equals(itemApply.getMember()))
            throw new GeneralHandler(ErrorStatus.NOT_OFFERED);
        if (itemApply.getStatus() != ItemApplyStatus.PENDING)
            throw new GeneralHandler(ErrorStatus.ALREADY_CHOSE_OFFER_ACCEPTANCE);

        if (acceptItemOfferRequestDTO.getAccept()) itemApply.setStatus(ItemApplyStatus.ACCEPTED);
        else itemApply.setStatus(ItemApplyStatus.REJECTED);

        //알림 전송
        NotificationEventRequestDTO.NotificationEventDTO eventDTO =
                NotificationEventRequestDTO.NotificationEventDTO.builder()
                        .senderId(offeredMember.getId())
                        .receiverId(itemApply.getItem().getMember().getId())
                        .notificationType(NotificationType.INVITE)
                        .message(offeredMember.getNameNotNull() +
                                "님이 " +
                                itemApply.getItem().getName() +
                                "의 제안에 대해 응답했어요! 응답을 확인해보세요!"
                                // 이렇게 보내는 게 좋은가? 다른 좋은 Message 없나?
                                // 그렇다고 알림에서부터 수락/거절 사실을 통보하면 마음아플 것 같음...
                                // 취업준비할 때 거절은 이를 돌려 말하는 것처럼...
                        )
                        .referenceType(ReferenceType.ITEM)
                        .referenceId(itemApply.getItem().getId())
                        .build();
        notificationCommandService.saveNotification(eventDTO);
        notificationEventSender.send(eventDTO);
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
