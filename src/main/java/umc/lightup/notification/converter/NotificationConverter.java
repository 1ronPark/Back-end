package umc.lightup.notification.converter;

import org.springframework.data.domain.Page;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationResponseDTO;

import java.util.ArrayList;
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
}
