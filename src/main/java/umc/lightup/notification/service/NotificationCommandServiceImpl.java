package umc.lightup.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.repository.NotificationRepostiory;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {
  private final NotificationRepostiory notificationRepostiory;

  @Override
  public Notification deleteNotification(long notificationId) {
    Notification notification = notificationRepostiory.findById(notificationId).orElseThrow(() -> new GeneralHandler(ErrorStatus.NOTIFICATION_NOT_FOUND));
    notificationRepostiory.deleteById(notificationId);
    return notification;
  }
}
