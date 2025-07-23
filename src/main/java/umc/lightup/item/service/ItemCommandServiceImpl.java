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
import umc.lightup.item.domain.ItemImage;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.repository.ItemImageRepository;
import umc.lightup.item.repository.ItemRepository;
import umc.lightup.item.repository.RecruitPositionRepository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRegionRepository;
import umc.lightup.member.repository.MemberRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCommandServiceImpl implements ItemCommandService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImageRepository itemImageRepository;
    private final MemberRegionRepository memberRegionRepository;
    private final RecruitPositionRepository recruitPositionRepository;

    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    @Override
    @Transactional
    public Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile file) {
        Item item = ItemConverter.toItem(request, member);

        Item saveditem = itemRepository.save(item);

        if (file != null) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            String fileUrl = s3Manager.uploadFile(s3Manager.generateItemFileKeyName(savedUuid), file);
            itemImageRepository.save(ItemConverter.toItemImage(item, fileUrl));
        }

        return saveditem;
    }

    @Override
    public List<ItemResponseDTO.ItemResultDTO> getAllItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);

        return itemPage.stream()
                .map(item -> {
                    String itemImageUrl = item.getItemImages().stream()
                            .findFirst()
                            .map(ItemImage::getImageUrl)
                            .orElse(null);

                    return ItemConverter.toItemResultDTO(item, itemImageUrl);
                }).toList();
    }

    @Override
    public List<ItemResponseDTO.MyItemResultDTO> getMyItems(Member member) {
        List<Item> myItemList = itemRepository.findByMember(member);

        return myItemList.stream()
            .map(item -> {
                //item에 이미지 저장 방식이나 어떤 이미지를 item의 프로필 이미지로 설정할 것 인지에 따라서 수정해야할 가능성 있음
                String itemImageUrl = item.getItemImages().stream()
                        .findFirst()
                        .map(ItemImage::getImageUrl)
                        .orElse(null);

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
