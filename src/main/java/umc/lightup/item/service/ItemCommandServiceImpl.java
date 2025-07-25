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
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemRegion;
import umc.lightup.item.domain.RecruitPosition;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.repository.ItemRepository;
import umc.lightup.item.repository.RecruitPositionRepository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRegionRepository;
import umc.lightup.position.domain.Position;
import umc.lightup.position.repository.PositionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCommandServiceImpl implements ItemCommandService {

    private final ItemRepository itemRepository;
    private final MemberRegionRepository memberRegionRepository;
    private final RecruitPositionRepository recruitPositionRepository;
    private final PositionRepository positionRepository;

    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

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
    public List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);

        return itemPage.stream()
                .map(item -> {
                    String itemImageUrl = item.getItemProfileImageUrl();

                    return ItemConverter.toItemResultDTO(item, itemImageUrl);
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
}
