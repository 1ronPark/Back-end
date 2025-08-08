package umc.lightup.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import umc.lightup.notification.enums.NotificationType;
import umc.lightup.notification.enums.ReferenceType;

public class NotificationEventRequestDTO {

  @Builder
  @Getter
  @AllArgsConstructor
  public static class NotificationEventDTO {
    @NotNull
    private Long senderId;
    @NotNull
    private Long receiverId;
    private NotificationType notificationType;
    private String message;
    private ReferenceType referenceType;
    private Long referenceId;
  }
}
