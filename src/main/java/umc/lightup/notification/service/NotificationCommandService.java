package umc.lightup.notification.service;

import umc.lightup.notification.domain.Notification;

public interface NotificationCommandService {
  Notification deleteNotification(long notificationId);
}
