package umc.lightup.light_talk.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.SuccessStatus;
import umc.lightup.light_talk.converter.CommentConverter;
import umc.lightup.light_talk.domain.Comment;
import umc.lightup.light_talk.dto.CommentRequestDTO;
import umc.lightup.light_talk.dto.CommentResponseDTO;
import umc.lightup.light_talk.service.CommentCommandService;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/light-talk/posts")
public class CommentRestController {
    private final MemberCommandService memberCommandService;
    private final CommentCommandService commentCommandService;

    @PostMapping("/{postId}/comments")
    @Operation(summary = "라잇톡 포스트에 댓글 등록 API", description = "라잇톡 특정 포스트에 댓글을 등록하는 API 입니다.")
    public ApiResponse<CommentResponseDTO.CommentJoinResultDTO> createPostComment(
            Authentication authentication,
            @PathVariable("postId") Long postId,
            @RequestBody CommentRequestDTO.CommentJoinDTO request) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        Comment comment = commentCommandService.createComment(postId, member, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentJoinResultDTO(comment));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "라잇톡 포스트의 댓글 삭제 API", description = "라잇톡 포스트의 댓글 삭제 API입니다.")
    public ApiResponse<Void> removePostComment(Authentication authentication, @PathVariable("commentId") Long commentId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        commentCommandService.removePostComment(member, commentId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @PostMapping("/comments/{commentId}/like")
    @Operation(summary = "라잇톡 댓글 좋아요 등록 API", description = "유저가 특정 라잇톡 포스트 댓글에 좋아요를 등록하는 API 입니다.")
    public ApiResponse<Void> addItemLike(Authentication authentication, @PathVariable("commentId") Long commentId) {
        String email = authentication.getName();
        Member member = memberCommandService.getMember(email);

        commentCommandService.addCommentLike(member, commentId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }

    @DeleteMapping("/comments/{commentId}/like")
    @Operation(summary = "라잇톡 댓글 좋아요 취소 API", description = "유저가 특정 라잇톡 포스트 댓글의 좋아요를 취소하는 API 입니다.")
    public ApiResponse<Void> removePostLike(Authentication authentication, @PathVariable("commentId") Long commentId) {
        String email = authentication.getName();
        commentCommandService.removeCommentLike(email, commentId);
        return ApiResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}
