package umc.lightup.item.converter;

import umc.lightup.item.domain.*;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.member.domain.Member;
import umc.lightup.member.enums.Mbti;

import java.util.List;

public class ItemConverter {
    public static ItemResponseDTO.ItemJoinResultDTO toItemJoinResultDTO(Member member, Item item) {
        return ItemResponseDTO.ItemJoinResultDTO.builder()
                .memberId(member.getId())
                .itemName(item.getName())
                .build();
    }

    public static ItemResponseDTO.ItemChangeResultDTO toItemChangeResultDTO(Member member, Item item) {
        return ItemResponseDTO.ItemChangeResultDTO.builder()
                .memberId(member.getId())
                .itemName(item.getName())
                .build();
    }

    public static ItemResponseDTO.ItemResultDTO toItemResultDTO(Item item, String itemImageUrl, int commentCount, boolean itemLike) {
        return ItemResponseDTO.ItemResultDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .memberName(item.getMember().getName())
                .schoolName(item.getMember().getSchool() == null ? null : item.getMember().getSchool().getName())
                .introduce(item.getIntroduce())
                .memberProfileImageUrl(item.getMember().getProfileImageUrl())
                .itemImageUrl(itemImageUrl)
                .updatedAt(item.getUpdatedAt().toLocalDate())
                .recruitStatus(item.isProjectStatus())
                .viewCount(item.getViewCount())
                .commentCount(commentCount)
                .likedByCurrentUser(itemLike)
                .build();
    }

    public static ItemResponseDTO.ItemResultListDTO toItemResultListDTO(List<ItemResponseDTO.ItemResultDTO> itemResultDTOList) {
        return ItemResponseDTO.ItemResultListDTO.builder()
                .items(itemResultDTOList)
                .build();
    }

    public static ItemResponseDTO.MyItemResultDTO toMyItemResultDTO(Item item, String itemImageUrl, List<ItemResponseDTO.ItemCategoriesResultDTO> itemCategoriesResultDTOList, boolean applicantStatus, boolean isMyApplyItem) {
        return ItemResponseDTO.MyItemResultDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .introduce(item.getIntroduce())
                .itemImageUrl(itemImageUrl)
                .itemCategories(itemCategoriesResultDTOList)
                .recruitStatus(item.isProjectStatus())
                .applicantStatus(applicantStatus)
                .isMyApplyItem(isMyApplyItem)
                .build();
    }

    public static ItemResponseDTO.MyItemResultListDTO toMyItemResultListDTO(List<ItemResponseDTO.MyItemResultDTO> myItemResultDTOList){
        return ItemResponseDTO.MyItemResultListDTO.builder()
                .items(myItemResultDTOList)
                .build();
    }

    public static ItemResponseDTO.ItemInfoDTO toItemInfoDTO(Item item,
                                                            List<ItemResponseDTO.ItemRegionResultDTO> itemRegionResultDTOList,
                                                            List<ItemResponseDTO.ItemCategoriesResultDTO> itemCategoriesResultDTOList,
                                                            List<ItemResponseDTO.RecruitPositionResultDTO> recruitPositionResultDTOList,
                                                            List<ItemResponseDTO.ItemCommentResultDTO> itemCommentResultDTOList,
                                                            int commentCount, boolean itemLike, boolean itemApplyStatus, boolean itemSuggestStatus) {
        return ItemResponseDTO.ItemInfoDTO.builder()
                .introduce(item.getIntroduce())
                .itemName(item.getName())
                .itemProfileImageUrl(item.getItemProfileImageUrl())
                .memberName(item.getMember().getName())
                .nickName(item.getMember().getNickname())
                .gender(item.getMember().getGender())
                .age(item.getMember().getAge())
                .mbti(Mbti.fromByte(item.getMember().getMbti()))
                .email(item.getMember().getEmail())
                .schoolName(item.getMember().getSchool() == null ? null : item.getMember().getSchool().getName())
                .regions(itemRegionResultDTOList)
                .description(item.getDescription())
                .recruitPositions(recruitPositionResultDTOList)
                .itemCategories(itemCategoriesResultDTOList)
                .itemComments(itemCommentResultDTOList)
                .commentCount(commentCount)
                .updatedAt(item.getUpdatedAt().toLocalDate())
                .likedByCurrentUser(itemLike)
                .applicantStatus(itemApplyStatus)
                .suggestStatus(itemSuggestStatus)
                .build();
    }

    public static ItemResponseDTO.ItemRegionResultDTO toItemRegionResultDTO(ItemRegion itemRegion) {
        return ItemResponseDTO.ItemRegionResultDTO.builder()
                .siDo(itemRegion.getSiDo())
                .siGunGu(itemRegion.getSiGunGu())
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

    public static ItemResponseDTO.ItemApplyStatusListDTO toItemApplyStatusListDTO
            (List<ItemResponseDTO.ItemApplyStatusDTO> itemApplyStatusDTO) {
        return ItemResponseDTO.ItemApplyStatusListDTO.builder()
                .itemApplyStatuses(itemApplyStatusDTO)
                .build();
    }

    public static ItemResponseDTO.ItemApplyStatusDTO toItemApplyStatusDTO (ItemApply itemApply, Member member) {
        Member itemOwner = itemApply.getItem().getMember();
        Member targetMember = itemApply.getMember();
        return ItemResponseDTO.ItemApplyStatusDTO.builder()
                .itemOwned(member.equals(itemOwner))
                .itemId(itemApply.getItem().getId())
                .itemName(itemApply.getItem().getName())
                .itemImageUrl(itemApply.getItem().getItemProfileImageUrl())
                .itemOwnerUsername(itemOwner == null ? null : itemOwner.getUsername())
                .memberId(targetMember == null ? null : targetMember.getId())
                .memberUsername(targetMember == null ? null : targetMember.getNameNotNull())
                .memberProfileImageUrl(targetMember == null ? null : targetMember.getProfileImageUrl())
                .applyId(itemApply.getId())
                .fromOwner(itemApply.isFromOwner())
                .applyStatus(itemApply.getStatus())
                .build();
    }

    public static ItemResponseDTO.ItemOfferResultDTO toItemOfferResultDTO (ItemApply itemApply) {
        return ItemResponseDTO.ItemOfferResultDTO.builder()
                .appliedAt(itemApply.getAppliedAt())
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

    public static ItemResponseDTO.ItemCategoriesResultDTO toItemCategoriesResultDTO (ItemCategory itemCategory) {
        return ItemResponseDTO.ItemCategoriesResultDTO.builder()
                .categoryName(itemCategory.getCategoryType().getDisplayName())
                .build();
    }

    public static ItemResponseDTO.ItemEditInfoDTO toItemEditInfoDTO (Item item,
                                                                     List<ItemResponseDTO.ItemCategoriesResultDTO> itemCategoriesResultDTOList,
                                                                     List<ItemResponseDTO.ItemRegionResultDTO> itemRegionResultDTOList,
                                                                     List<ItemResponseDTO.RecruitPositionResultDTO> recruitPositionResultDTOList) {
        return ItemResponseDTO.ItemEditInfoDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .introduce(item.getIntroduce())
                .extraLink1(item.getExtraLink1())
                .extraLink2(item.getExtraLink2())
                .description(item.getDescription())
                .itemCategories(itemCategoriesResultDTOList)
                .itemProfileImageUrl(item.getItemProfileImageUrl())
                .itemPlanFileUrl(item.getItemPlanFileUrl())
                .projectStatus(item.isProjectStatus())
                .regions(itemRegionResultDTOList)
                .recruitPositions(recruitPositionResultDTOList)
                .build();
    }

/*    public static ItemImage toItemImage(Item item, String imageUrl) {
        return ItemImage.builder()
                .item(item)
                .imageUrl(imageUrl)
                .build();
    }*/
}
