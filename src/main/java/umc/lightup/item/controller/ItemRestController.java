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
import umc.lightup.item.converter.ItemConverter;
import umc.lightup.item.domain.Item;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.service.ItemCommandService;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemRestController {

    private final ItemCommandService itemCommandService;
    private final MemberCommandService memberCommandService;

    private static final int DEFAULT_ITEM_PAGE_SIZE = 12;

    @GetMapping("/search")
    @Operation(summary = "전체 프로젝트 조회 API", description = "전체 프로젝트를 조회하는 API이며 페이징을 포함합니다. 요청 파라미터로 page 번호를 입력할 수 있습니다.")
    public ApiResponse<ItemResponseDTO.ItemResultListDTO> viewAllItems(@RequestParam(defaultValue = "0") @Min(1) Integer page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_ITEM_PAGE_SIZE, Sort.by("createdAt").descending());

        List<ItemResponseDTO.ItemResultDTO> allItems = itemCommandService.getAllItems(pageable);
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
    @Operation(summary = "본인 프로젝트 생성 API", description = "Item을 생성할 수 있으며 이미지도 업로드 가능합니다. (이미지 업로드는 필수 X)")
    public ApiResponse<ItemResponseDTO.ItemJoinResultDTO> createItem(Authentication authentication,
                                                   @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                                     @RequestPart("request") @Valid ItemRequestDTO.ItemJoinRequestDTO request,
                                                   @RequestPart(value = "itemImage", required = false)MultipartFile itemImage) {
        Member member = memberCommandService.getMember(authentication.getName());
        Item item = itemCommandService.createItem(member, request, itemImage);
        return ApiResponse.onSuccess(ItemConverter.toItemJoinResultDTO(member, item));
    }
}
