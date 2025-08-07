package umc.lightup.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NotificationEventResponseDTO {
  @Builder
  @Getter
  @AllArgsConstructor
  public static class NotificationEventDTO {
    private Long receiverId;
    private String message;
  }
}
