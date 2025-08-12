package umc.lightup.lighttalk.service;

import umc.lightup.lighttalk.domain.Comment;
import umc.lightup.lighttalk.dto.CommentRequestDTO;
import umc.lightup.member.domain.Member;

public interface CommentCommandService {
    Comment createComment(Long postId, Member member, CommentRequestDTO.CommentJoinDTO request);
    void removePostComment(Member member, Long commentId);
    void addCommentLike(Member member, Long commentId);
    void removeCommentLike(String email, Long commentId);
}
