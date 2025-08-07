package umc.lightup.notification.converter;

import org.springframework.data.domain.Page;
import umc.lightup.member.domain.Member;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationEventRequestDTO;
import umc.lightup.notification.dto.NotificationEventResponseDTO;
import umc.lightup.notification.dto.NotificationResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationConverter {

  public static NotificationResponseDTO.NotificationDTO notificationDTO(Notification notification){
    return NotificationResponseDTO.NotificationDTO.builder()
            .notificationId(notification.getId())
            .message(notification.getMessage())
            .notificationType(String.valueOf(notification.getType()))
            .isRead(notification.getIsRead())
            .build();
  }

  public static NotificationResponseDTO.NotificationListDTO notificationListDTO(Page<Notification> notificationList){
    List<NotificationResponseDTO.NotificationDTO> notificationListDTO = notificationList.stream()
            .map(NotificationConverter::notificationDTO).collect(Collectors.toList());
    return NotificationResponseDTO.NotificationListDTO.builder()
            .isLast(notificationList.isLast())
            .isFirst(notificationList.isFirst())
            .totalPage(notificationList.getTotalPages())
            .totalElements(notificationList.getTotalElements())
            .listSize(notificationListDTO.size())
            .notificationList(notificationListDTO)
            .build();
  }

  public static NotificationResponseDTO.NotificationTotal notificationTotal(Long totalSize){
    return NotificationResponseDTO.NotificationTotal.builder()
            .total(totalSize)
            .build();
  }

  public static NotificationResponseDTO.NotificationDeleteDTO notificationDeleteDTO(Notification notification){
    return NotificationResponseDTO.NotificationDeleteDTO.builder()
            .notificationId(notification.getId())
            .message(notification.getMessage())
            .build();
  }

  public static NotificationResponseDTO.NotificationPatchDTO notificationPatchDTO(Notification notification){
    return NotificationResponseDTO.NotificationPatchDTO.builder()
            .notificationId(notification.getId())
            .message(notification.getMessage())
            .isRead(notification.getIsRead())
            .build();
  }

  public static Notification notificationEventDTO(Member sender, Member receiver, NotificationEventRequestDTO.NotificationEventDTO notificationEventDTO){
    return Notification.builder()
            .sender(sender)
            .receiver(receiver)
            .type(notificationEventDTO.getNotificationType())
            .message(notificationEventDTO.getMessage())
            .referenceType(notificationEventDTO.getReferenceType())
            .referenceId(notificationEventDTO.getReferenceId())
            .isRead(false)
            .build();
  }

  public static NotificationEventResponseDTO.NotificationEventDTO notificationEventDTO(Member recevier, Notification notification){
    return NotificationEventResponseDTO.NotificationEventDTO.builder()
            .receiverId(recevier.getId())
            .message(notification.getMessage())
            .build();
  }

  public static NotificationResponseDTO.SSETestDTO sseTestDTO(String message){
    return NotificationResponseDTO.SSETestDTO.builder()
            .message(message)
            .build();
  }
}
