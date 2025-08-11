package umc.lightup.light_talk.converter;

import umc.lightup.light_talk.domain.Comment;
import umc.lightup.light_talk.dto.CommentResponseDTO;

public class CommentConverter {

    public static CommentResponseDTO.CommentJoinResultDTO toCommentJoinResultDTO(Comment comment) {
        return CommentResponseDTO.CommentJoinResultDTO.builder()
                .commentId(comment.getId())
                .build();
    }
}
