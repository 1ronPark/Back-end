package umc.lightup.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.lightup.notification.enums.NotificationType;
import umc.lightup.notification.enums.ReferenceType;

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
    NotificationType notificationType; // 알림 종류(프론트에게 전송 시 자동으로 String으로 변환됨)
    Boolean isRead;           // 알림 읽음 여부
    ReferenceType referenceType; // 래퍼런스 종류(프론트에게 전송 시 자동으로 String으로 변환됨)
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
