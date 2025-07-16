package umc.lightup.notification.service;

import org.springframework.data.domain.Page;
import umc.lightup.notification.domain.Notification;

public interface NotificationQueryService {
  Page<Notification> getNotificationList(Long userId, Integer page, Integer size);
}
