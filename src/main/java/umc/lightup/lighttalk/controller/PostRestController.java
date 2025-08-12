package umc.lightup.lighttalk.controller;

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
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.lighttalk.converter.PostConverter;
import umc.lightup.lighttalk.domain.Post;
import umc.lightup.lighttalk.dto.PostRequestDTO;
import umc.lightup.lighttalk.dto.PostResponseDTO;
import umc.lightup.lighttalk.service.PostCommandService;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/light-talk/posts")
public class PostRestController {
    private final MemberCommandService memberCommandService;
    private final PostCommandService postCommandService;

    private static final int DEFAULT_POST_PAGE_SIZE = 10;

    @GetMapping("/search")
    @Operation(summary = "라잇톡 전체 포스트 조회 API", description = "라잇톡 전체 포스트 조회 API 입니다.")
    public ApiResponse<PostResponseDTO.PostResultListDTO> viewAllPosts(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") @Min(1) Integer page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_POST_PAGE_SIZE, Sort.by("createdAt").descending());

        Set<Long> likedPostIds = Collections.emptySet();

        if (authentication != null) {
            String email = authentication.getName();
            Member member = memberCommandService.getMember(email);
            likedPostIds = postCommandService.findPostLikes(member.getId());
        }

        List<PostResponseDTO.PostResultDTO> postResultDTOList = postCommandService.getAllPosts(pageable, likedPostIds);
        return ApiResponse.onSuccess(PostConverter.toPostResultListDTO(postResultDTOList));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "라잇톡 포스트 생성 API" , description = "라잇톡 포스트 생성 API 입니다. 이미지는 최대 3개까지만 업로드 가능합니다.")
    public ApiResponse<PostResponseDTO.PostJoinResultDTO> createPost(
            Authentication authentication,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") @Valid PostRequestDTO.PostJoinRequestDTO request,
            @RequestPart(value = "postImages", required = false) List<MultipartFile> postImages) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        //업로드 이미지가 3개 초과인지 검증
        if (postImages != null && postImages.size() > 3) {
            throw new GeneralHandler(ErrorStatus.TOO_MANY_POST_IMAGE);
        }

        Post post = postCommandService.createPost(member, request, postImages);
        return ApiResponse.onSuccess(PostConverter.toPostJoinResultDTO(post));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "특정 라잇톡 포스트 상세 조회 API", description = "특정 라잇톡 포스트 상세 조회 API 입니다. ")
    public ApiResponse<PostResponseDTO.PostInfoDTO> getPostInfo (@PathVariable("postId") @Min(1) Long postId) {
        Post findPost = postCommandService.getSinglePostWithComments(postId);
        List<PostResponseDTO.MemberPositionDTO> postMemberPositions = postCommandService.getPostMemberPositions(findPost);
        List<PostResponseDTO.PostImageDTO> postImages = postCommandService.getPostImages(findPost);
        List<PostResponseDTO.PostCommentResultDTO> postComments = postCommandService.getPostComments(findPost);

        return ApiResponse.onSuccess(PostConverter.toPostInfoDTO(findPost, postMemberPositions, postImages, postComments));
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "라잇톡 포스트 좋아요 등록 API", description = "유저가 특정 라잇톡 포스트에 좋아요를 등록하는 API 입니다.")
    public ApiResponse<Void> addItemLike(Authentication authentication, @PathVariable("postId") Long postId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        postCommandService.addPostLike(member, postId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @DeleteMapping("/{postId}/like")
    @Operation(summary = "라잇톡 포스트 좋아요 취소 API", description = "유저가 특정 라잇톡 포스트의 좋아요를 취소하는 API 입니다.")
    public ApiResponse<Void> removePostLike(Authentication authentication, @PathVariable("postId") Long postId) {
        String email = authentication.getName();
        postCommandService.removePostLike(email, postId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}
