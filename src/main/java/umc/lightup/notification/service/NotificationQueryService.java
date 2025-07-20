package umc.lightup.notification.service;

import org.springframework.data.domain.Page;
import umc.lightup.member.domain.Member;
import umc.lightup.notification.domain.Notification;

public interface NotificationQueryService {
  Page<Notification> getNotificationList(Member member, Integer page, Integer size);
}
