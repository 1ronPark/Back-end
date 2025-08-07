package umc.lightup.item.converter;

import umc.lightup.item.domain.*;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberRegion;

import java.util.List;

public class ItemConverter {
    public static ItemResponseDTO.ItemJoinResultDTO toItemJoinResultDTO(Member member, Item item) {
        return ItemResponseDTO.ItemJoinResultDTO.builder()
                .memberId(member.getId())
                .itemName(item.getName())
                .build();
    }

    public static ItemResponseDTO.ItemResultDTO toItemResultDTO(Item item, String itemImageUrl, int viewCount, int commentCount, boolean itemLike) {
        return ItemResponseDTO.ItemResultDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .memberName(item.getMember().getName())
                .itemImageUrl(itemImageUrl)
                .updatedAt(item.getUpdatedAt().toLocalDate())
                .recruitStatus(item.isProjectStatus())
                .viewCount(viewCount)
                .commentCount(commentCount)
                .likedByCurrentUser(itemLike)
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

    public static ItemResponseDTO.ItemInfoDTO toItemInfoDTO(Item item, List<ItemResponseDTO.ItemRegionResultDTO> itemRegionResultDTOList, List<ItemResponseDTO.RecruitPositionResultDTO> recruitPositionResultDTOList, List<ItemResponseDTO.ItemCommentResultDTO> itemCommentResultDTOList, int commentCount, boolean itemLike) {
        return ItemResponseDTO.ItemInfoDTO.builder()
                .introduce(item.getIntroduce())
                .itemName(item.getName())
                .itemProfileImageUrl(item.getItemProfileImageUrl())
                .memberName(item.getMember().getName())
                .gender(item.getMember().getGender())
                .age(item.getMember().getAge())
                .mbti(item.getMember().getMbti())
                .email(item.getMember().getEmail())
                .school(item.getMember().getSchool())
                .regions(itemRegionResultDTOList)
                .description(item.getDescription())
                .recruitPositions(recruitPositionResultDTOList)
                .itemComments(itemCommentResultDTOList)
                .commentCount(commentCount)
                .updatedAt(item.getUpdatedAt().toLocalDate())
                .likedByCurrentUser(itemLike)
                .build();
    }

    public static ItemResponseDTO.ItemRegionResultDTO toItemRegionResultDTO(MemberRegion memberRegion) {
        return ItemResponseDTO.ItemRegionResultDTO.builder()
                .siDo(memberRegion.getSiDo())
                .siGunGu(memberRegion.getSiGunGu())
                .build();
    }

    public static ItemResponseDTO.RecruitPositionResultDTO toRecruitPositionResultDTO(RecruitPosition recruitPosition) {
        return ItemResponseDTO.RecruitPositionResultDTO.builder()
                .positionName(recruitPosition.getPosition().getName())
                .recruitNumber(recruitPosition.getRecruitNumber())
                .mainTasks(recruitPosition.getMainTasks())
                .preferentialTreatment(recruitPosition.getPreferentialTreatment())
                .preferMbti(recruitPosition.getPreferMbti())
                .build();
    }

    public static Item toItem(ItemRequestDTO.ItemJoinRequestDTO request, Member member) {
        return Item.builder()
                .member(member)
                .name(request.getName())
                .introduce(request.getIntroduce())
                .description(request.getDescription())
                .projectStatus(request.isProjectStatus())
                .extraLink1(request.getExtraLink1())
                .extraLink2(request.getExtraLink2())
                .build();
    }

    public static ItemResponseDTO.ItemApplyResultDTO toItemApplyResultDTO (ItemApply itemApply) {
        return ItemResponseDTO.ItemApplyResultDTO.builder()
                .appliedAt(itemApply.getAppliedAt())
                .message(itemApply.getItem().getName() + "에 지원했어요")
                .build();
    }

    public static ItemResponseDTO.ItemCommentResultDTO toItemCommentResultDTO (ItemComment itemComment) {
        return ItemResponseDTO.ItemCommentResultDTO.builder()
                .itemCommentId(itemComment.getId())
                .authorName(itemComment.getCommentMember().getName())
                .authorProfileImageURL(itemComment.getCommentMember().getProfileImageUrl())
                .content(itemComment.getContent())
                .updatedAt(itemComment.getUpdatedAt())
                .build();
    }

/*    public static ItemImage toItemImage(Item item, String imageUrl) {
        return ItemImage.builder()
                .item(item)
                .imageUrl(imageUrl)
                .build();
    }*/
}
