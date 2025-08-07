package umc.lightup.notification.service;

import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationEventRequestDTO;
import umc.lightup.notification.dto.NotificationEventResponseDTO;

public interface NotificationCommandService {
  Notification deleteNotification(long notificationId);
  Notification updateNotification(long notificationId);
  NotificationEventResponseDTO.NotificationEventDTO saveNotification(NotificationEventRequestDTO.NotificationEventDTO notification);
}
