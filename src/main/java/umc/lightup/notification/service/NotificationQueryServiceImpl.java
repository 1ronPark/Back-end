package umc.lightup.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRepository;
import umc.lightup.notification.converter.NotificationConverter;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationResponseDTO;
import umc.lightup.notification.repository.EmitterRepository;
import umc.lightup.notification.repository.NotificationRepostiory;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {
  // SSE 연결 시간 설정
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
  private final MemberRepository memberRepository;
  private final EmitterRepository emitterRepository;
  private final NotificationRepostiory notificationRepostiory;

  // sse 구독 api 전용
  @Override
  public SseEmitter subscribe(Member member, String lastEventId) {
    // 매 연결마다 고유 이벤트 id 부여
    String eventId = member.getId() + "_" + System.currentTimeMillis();

    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    emitter.onCompletion(() -> {
      log.info("Completion SSE. User : {}", member.getId());
      emitterRepository.deleteById(eventId);
    });

    emitter.onTimeout(() -> {
      log.info("Timeout SSE. User : {}", member.getId());
      emitter.complete();
      emitterRepository.deleteById(eventId);
    });

    emitter.onError(e -> {
      log.info("Error SSE. User : {}, Message: {}", member.getId(), e.getMessage());
      emitter.complete();
      emitterRepository.deleteById(eventId);
    });

    emitterRepository.save(eventId, emitter);

    sendToClient(emitter, eventId, "알림 서버 연결 성공. [userId=" + member.getId() + "]");

    // 클라이언트가 미수신한 Event 목록이 존재할 경우 모두 전송
    if(lastEventId != null) {
      Map<String, SseEmitter> sseEmitter = emitterRepository.findAllById(eventId);
      sseEmitter.entrySet().stream()
              .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
              .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
    }

    return emitter;
  }

  // SSE 테스트 api 전용
  @Override
  public NotificationResponseDTO.SSETestDTO testSend(long receiverId, String message) {
    Map<String, SseEmitter> sseEmitters = emitterRepository.findAllById(String.valueOf(receiverId));

    if (sseEmitters.isEmpty()) {
      log.info("No active SSE connections for userId: {}", receiverId);
    }

    sseEmitters.forEach((emitterId, emitter) -> {
      sendToClient(emitter, emitterId, message);
    });
    return NotificationConverter.sseTestDTO(message);
  }

  @Override
  public NotificationResponseDTO.NotificationTotal getTotal(Member member) {
    Long totalSize = notificationRepostiory.countByReceiver(member);
    return NotificationConverter.notificationTotal(totalSize);
  }

  // 알림목록 조회 api 전용
  @Override
  public Page<Notification> getNotificationList(Member member, Integer page, Integer size) {
    Page<Notification> NotificationPage = notificationRepostiory.findAllByReceiver(member, PageRequest.of(page, size));
    return NotificationPage;
  }

  @Override
  public void sendToClient(SseEmitter emitter, String id, Object data) {
    try {
      emitter.send(SseEmitter.event()
              .id(id)
              .name("sse")
              .data(data));
    } catch (IOException exception) {
      log.info("SSE Emitter Exception ID: {}", id);
      emitterRepository.deleteById(id);
    }
  }
}
