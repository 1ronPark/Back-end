package umc.lightup.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.item.converter.ItemConverter;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemApply;
import umc.lightup.item.domain.ItemComment;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.service.ItemCommandService;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemRestController {

    private final ItemCommandService itemCommandService;
    private final MemberCommandService memberCommandService;

    private static final int DEFAULT_ITEM_PAGE_SIZE = 12;

    @GetMapping("/search")
    @Operation(summary = "전체 프로젝트 조회 API", description = "전체 프로젝트를 조회하는 API이며 페이징을 포함합니다. 요청 파라미터로 page 번호를 입력할 수 있습니다.")
    public ApiResponse<ItemResponseDTO.ItemResultListDTO> viewAllItems(Authentication authentication, @RequestParam(defaultValue = "0") @Min(1) Integer page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_ITEM_PAGE_SIZE, Sort.by("createdAt").descending());

        Set<Long> likedItemIds = Collections.emptySet();

        if (authentication != null) {
            String email = authentication.getName();
            Member member = memberCommandService.getMember(email);
            likedItemIds = itemCommandService.findItemLikes(member.getId());
        }

        List<ItemResponseDTO.ItemResultDTO> allItems = itemCommandService.getAllItems(pageable, likedItemIds);
        return ApiResponse.onSuccess(ItemConverter.toItemResultListDTO(allItems));
    }

    @GetMapping("/me")
    @Operation(summary = "본인 프로젝트 조회 API", description = "사용자의 프로젝트를 조회하는 API 입니다. 등록한 사진이 없다면 itemImageUrl은 null을 반환합니다.")
    public ApiResponse<ItemResponseDTO.MyItemResultListDTO> viewMyItems(Authentication authentication) {
        Member member = memberCommandService.getMember(authentication.getName());

        List<ItemResponseDTO.MyItemResultDTO> myItems = itemCommandService.getMyItems(member);
        return ApiResponse.onSuccess(ItemConverter.toMyItemResultListDTO(myItems));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "본인 프로젝트 생성 API", description = "Item을 생성할 수 있으며 Item 프로필 이미지와 Item 기획서도 업로드 가능합니다. (요구사항이 Item 프로필 이미지, 기획서 업로드 필수)")
    public ApiResponse<ItemResponseDTO.ItemJoinResultDTO> createItem(
            Authentication authentication,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") @Valid ItemRequestDTO.ItemJoinRequestDTO request,
            @RequestPart(value = "itemProfileImage")MultipartFile itemProfileImage,
            @RequestPart(value = "itemPlanFile")MultipartFile itemPlanFile) {
        Member member = memberCommandService.getMember(authentication.getName());
        Item item = itemCommandService.createItem(member, request, itemProfileImage, itemPlanFile);
        return ApiResponse.onSuccess(ItemConverter.toItemJoinResultDTO(member, item));
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "특정 프로젝트 상세 조회 API", description = "특정 프로젝트를 상세 조회하는 API 입니다.")
    public ApiResponse<ItemResponseDTO.ItemInfoDTO> getItemInfo(Authentication authentication, @PathVariable("itemId") @Min(1) Long itemId) {
        Member member = memberCommandService.getMember(authentication.getName());
        Item findItem = itemCommandService.getSingleItemWithComments(itemId);
        List<ItemResponseDTO.ItemRegionResultDTO> itemRegions = itemCommandService.getItemRegions(findItem);
        List<ItemResponseDTO.RecruitPositionResultDTO> itemRecruitPositions = itemCommandService.getItemRecruitPositions(findItem);
        List<ItemResponseDTO.ItemCommentResultDTO> itemComments = itemCommandService.getItemComments(findItem);
        boolean itemLike = itemCommandService.getItemLike(member.getId(), findItem.getId());
        itemCommandService.updateItemHistory(member, findItem);
        return ApiResponse.onSuccess(ItemConverter.toItemInfoDTO(findItem, itemRegions, itemRecruitPositions, itemComments, itemLike));
    }

    @PostMapping("/{itemId}/like")
    @Operation(summary = "프로젝트 좋아요 등록 API", description = "유저가 특정 프로젝트에 좋아요를 등록하는 API 입니다.")
    public ApiResponse<Void> addItemLike(Authentication authentication, @PathVariable("itemId") long itemId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);
        itemCommandService.addItemLike(member, itemId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @DeleteMapping("/{itemId}/like")
    @Operation(summary = "프로젝트 좋아요 취소 API", description = "유저가 특정 프로젝트의 좋아요를 취소하는 API 입니다.")
    public ApiResponse<Void> removeItemLike(Authentication authentication, @PathVariable("itemId") long itemId) {
        String email = authentication.getName();
        itemCommandService.removeItemLike(email, itemId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @PostMapping("/{itemId}/apply")
    @Operation(summary = "프로젝트 지원 API", description = "유저가 프로젝트에 지원 요청을 보내는 API 입니다.")
    public ApiResponse<ItemResponseDTO.ItemApplyResultDTO> applyItem(Authentication authentication, @PathVariable("itemId") long itemId) {
        Member member = memberCommandService.getMember(authentication.getName());
        Item item = itemCommandService.getSingleItem(itemId);
        ItemApply itemApply = itemCommandService.applyItem(member, item);
        return ApiResponse.onSuccess(ItemConverter.toItemApplyResultDTO(itemApply));
    }

    @PostMapping("/{itemId}/comments")
    @Operation(summary = "프로젝트 댓글 작성 API", description = "특정 프로젝트 상세 조회 페이지에서 댓글을 작성할 수 있는 API입니다.")
    public ApiResponse<ItemResponseDTO.ItemCommentResultDTO> writeItemComment(Authentication authentication, @PathVariable("itemId") Long itemId, @RequestBody ItemRequestDTO.ItemCommentRequestDTO request) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        ItemComment itemComment = itemCommandService.createItemComment(member, itemId, request);
        return ApiResponse.onSuccess(ItemConverter.toItemCommentResultDTO(itemComment));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "프로젝트 댓글 삭제 API", description = "특정 프로젝트 상세 조회 페이지에서 댓글을 삭제하는 API입니다.")
    public ApiResponse<Void> removeItemComment(Authentication authentication, @PathVariable("commentId") Long commentId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        itemCommandService.removeItemComment(member, commentId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}
