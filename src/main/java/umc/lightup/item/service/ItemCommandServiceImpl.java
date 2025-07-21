package umc.lightup.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.aws.s3.AmazonS3Manager;
import umc.lightup.common.Uuid;
import umc.lightup.common.repository.UuidRepository;
import umc.lightup.item.converter.ItemConverter;
import umc.lightup.item.domain.Item;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.repository.ItemImageRepository;
import umc.lightup.item.repository.ItemRepository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCommandServiceImpl implements ItemCommandService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImageRepository itemImageRepository;

    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    @Override
    @Transactional
    public Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile file) {
        Item item = ItemConverter.toItem(request, member);

        Item saveditem = itemRepository.save(item);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String fileUrl = s3Manager.uploadFile(s3Manager.generateItemFileKeyName(savedUuid), file);
        itemImageRepository.save(ItemConverter.toItemImage(item, fileUrl));

        return saveditem;
    }
}
