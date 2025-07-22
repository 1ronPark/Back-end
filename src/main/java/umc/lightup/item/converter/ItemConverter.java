package umc.lightup.item.converter;

import org.springframework.data.domain.Page;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemImage;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;

import java.util.List;

public class ItemConverter {
    public static ItemResponseDTO.ItemJoinResultDTO toItemJoinResultDTO(Member member, Item item) {
        return ItemResponseDTO.ItemJoinResultDTO.builder()
                .memberId(member.getId())
                .itemName(item.getName())
                .build();
    }

    public static ItemResponseDTO.ItemResultDTO toItemResultDTO(Item item, String itemImageUrl) {
        return ItemResponseDTO.ItemResultDTO.builder()
                .itemName(item.getName())
                .memberName(item.getMember().getName())
                .itemImageUrl(itemImageUrl)
                .build();
    }

    public static ItemResponseDTO.ItemResultListDTO toItemResultListDTO(List<ItemResponseDTO.ItemResultDTO> itemResultDTOList) {
        return ItemResponseDTO.ItemResultListDTO.builder()
                .items(itemResultDTOList)
                .build();
    }

    public static ItemResponseDTO.MyItemResultDTO toMyItemResultDTO(Item item, String itemImageUrl) {
        return ItemResponseDTO.MyItemResultDTO.builder()
                .itemName(item.getName())
                .introduce(item.getIntroduce())
                .itemImageUrl(itemImageUrl)
                .build();
    }

    public static ItemResponseDTO.MyItemResultListDTO toMyItemResultListDTO(List<ItemResponseDTO.MyItemResultDTO> myItemResultDTOList){
        return ItemResponseDTO.MyItemResultListDTO.builder()
                .items(myItemResultDTOList)
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
