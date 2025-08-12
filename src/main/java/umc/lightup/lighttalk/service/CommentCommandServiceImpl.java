package umc.lightup.lighttalk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.lighttalk.domain.Comment;
import umc.lightup.lighttalk.domain.CommentLike;
import umc.lightup.lighttalk.domain.Post;
import umc.lightup.lighttalk.dto.CommentRequestDTO;
import umc.lightup.lighttalk.repository.CommentLikeRepository;
import umc.lightup.lighttalk.repository.CommentRepository;
import umc.lightup.lighttalk.repository.PostRepository;
import umc.lightup.member.domain.Member;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCommandServiceImpl implements CommentCommandService{
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public Comment createComment(Long postId, Member member, CommentRequestDTO.CommentJoinDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .commentMember(member)
                .post(post)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void removePostComment(Member member, Long commentId) {
        if (commentRepository.deleteByCommentMemberAndId(member, commentId) == 0) {
            throw new GeneralHandler(ErrorStatus.POST_COMMENT_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void addCommentLike(Member member, Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POST_COMMENT_NOT_FOUND));

        if (findComment.getCommentMember().equals(member)) {
            throw new GeneralHandler(ErrorStatus.MY_COMMENT_LIKE);
        }

        if (commentLikeRepository.existsByMemberIdAndCommentId(member.getId(), commentId)) {
            throw new GeneralHandler(ErrorStatus.COMMENT_ALREADY_LIKED);
        }

        commentRepository.increaseCommentLike(commentId);

        commentLikeRepository.save(CommentLike.builder()
                .comment(findComment)
                .member(member)
                .build());
    }

    @Override
    @Transactional
    public void removeCommentLike(String email, Long commentId) {
        if (commentLikeRepository.removeByMemberEmailAndCommentId(email, commentId) == 0) {
            throw new GeneralHandler(ErrorStatus.COMMENT_LIKE_NOT_FOUND);
        }

        commentRepository.decreaseCommentLike(commentId);
    }
}
