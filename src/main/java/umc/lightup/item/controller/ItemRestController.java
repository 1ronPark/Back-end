package umc.lightup.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.ApiResponse;
import umc.lightup.item.converter.ItemConverter;
import umc.lightup.item.domain.Item;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.service.ItemCommandService;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemRestController {

    private final ItemCommandService itemCommandService;
    private final MemberCommandService memberCommandService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Item을 생성하는 API 입니다.", description = "Item을 생성할 수 있으며 이미지도 업로드 가능합니다.")
    public ApiResponse<ItemResponseDTO.ItemJoinResultDTO> createItem(Authentication authentication,
                                                   @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                                     @RequestPart("request") @Valid ItemRequestDTO.ItemJoinRequestDTO request,
                                                   @RequestPart(value = "itemImage", required = false)MultipartFile itemImage) {
        Member member = memberCommandService.getMember(authentication.getName());
        Item item = itemCommandService.createItem(member, request, itemImage);
        return ApiResponse.onSuccess(ItemConverter.toItemJoinResultDTO(member, item));
    }
}
