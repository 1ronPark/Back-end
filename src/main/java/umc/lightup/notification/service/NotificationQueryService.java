package umc.lightup.notification.service;

import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.lightup.member.domain.Member;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationResponseDTO;

public interface NotificationQueryService {
  SseEmitter subscribe(Member member, String lastEventId);
  NotificationResponseDTO.SSETestDTO testSend(long id, String message);
  NotificationResponseDTO.NotificationTotal getTotal(Member member);
  Page<Notification> getNotificationList(Member member, Integer page, Integer size);

  void sendToClient(SseEmitter emitter, String id, Object data);
}
