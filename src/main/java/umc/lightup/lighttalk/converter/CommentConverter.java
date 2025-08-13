package umc.lightup.lighttalk.converter;

import umc.lightup.lighttalk.domain.Comment;
import umc.lightup.lighttalk.dto.CommentResponseDTO;

public class CommentConverter {

    public static CommentResponseDTO.CommentJoinResultDTO toCommentJoinResultDTO(Comment comment) {
        return CommentResponseDTO.CommentJoinResultDTO.builder()
                .commentId(comment.getId())
                .build();
    }

    public static CommentResponseDTO.CommentChangeResultDTO toCommentChangeResultDTO(Comment comment) {
        return CommentResponseDTO.CommentChangeResultDTO.builder()
                .commentId(comment.getId())
                .build();
    }
}
