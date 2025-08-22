package umc.lightup.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponseDTO {
  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationListDTO {
    List<NotificationDTO> notificationList;
    Integer listSize;
    Integer totalPage;
    Long totalElements;
    Boolean isFirst;
    Boolean isLast;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationDTO {
    Long notificationId;  // 알림 Id
    String message;       // 알림 메시지 내용
    String notificationType;  // 알림 종류
    Boolean isRead;           // 알림 읽음 여부
    Long referenceId;         // 래퍼런스 아이디
    LocalDateTime createdAt;  // 만들어진 시간
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationTotal{
    Long total;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationDeleteDTO{
    Long notificationId;
    String message;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationPatchDTO{
    Long notificationId;
    String message;
    Boolean isRead;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SSETestDTO {
    String message;
  }
}
