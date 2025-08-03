package umc.lightup.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.lightup.notification.dto.NotificationEventRequestDTO;
import umc.lightup.notification.dto.NotificationEventResponseDTO;
import umc.lightup.notification.repository.EmitterRepository;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
  private final NotificationCommandService notificationCommandService;
  private final NotificationQueryService notificationQueryService;
  private final EmitterRepository emitterRepository;

  @Async
  @EventListener
  public void handle(NotificationEventRequestDTO.NotificationEventDTO notificationEvent) {
    NotificationEventResponseDTO.NotificationEventDTO notificationEventDTO = notificationCommandService.saveNotification(notificationEvent); // 이 메서드는 @Transactional
    Map<String, SseEmitter> emitters = emitterRepository.findAllById(String.valueOf(notificationEventDTO.getReceiverId()));
    emitters.forEach((emitterId, emitter) -> {
      try {
        notificationQueryService.sendToClient(emitter, emitterId, notificationEventDTO.getMessage());
      } catch (Exception e) {
        emitterRepository.deleteById(emitterId);
        log.warn("[SSE 전송 실패] emitterId={}, error={}", emitterId, e.getMessage());
      }
    });
  }
}