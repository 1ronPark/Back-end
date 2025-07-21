package umc.lightup.item.converter;

import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemImage;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

public class ItemConverter {
    public static ItemResponseDTO.ItemJoinResultDTO toItemJoinResultDTO(Member member, Item item) {
        return ItemResponseDTO.ItemJoinResultDTO.builder()
                .memberId(member.getId())
                .itemName(item.getName())
                .build();
    }

    public static Item toItem(ItemRequestDTO.ItemJoinRequestDTO request, Member member) {
        return Item.builder()
                .member(member)
                .name(request.getName())
                .introduce(request.getIntroduce())
                .projectStatus(request.getProjectStatus())
                .collaboration(request.getCollaboration())
                .address(request.getAddress())
                .office(request.isOffice())
                .preferMbti(request.getPreferMbti())
                .description(request.getDescription())
                .build();
    }

    public static ItemImage toItemImage(Item item, String imageUrl) {
        return ItemImage.builder()
                .item(item)
                .imageUrl(imageUrl)
                .build();
    }
}
