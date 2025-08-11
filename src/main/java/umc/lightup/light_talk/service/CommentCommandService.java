package umc.lightup.light_talk.service;

import umc.lightup.light_talk.domain.Comment;
import umc.lightup.light_talk.dto.CommentRequestDTO;
import umc.lightup.member.domain.Member;

public interface CommentCommandService {
    Comment createComment(Long postId, Member member, CommentRequestDTO.CommentJoinDTO request);
    void removePostComment(Member member, Long commentId);
    void addCommentLike(Member member, Long commentId);
    void removeCommentLike(String email, Long commentId);
}
