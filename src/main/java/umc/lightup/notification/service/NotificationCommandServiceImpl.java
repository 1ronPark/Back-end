package umc.lightup.notification.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRepository;
import umc.lightup.notification.converter.NotificationConverter;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationEventRequestDTO;
import umc.lightup.notification.dto.NotificationEventResponseDTO;
import umc.lightup.notification.repository.NotificationRepostiory;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {
  private final MemberRepository memberRepository;
  private final NotificationRepostiory notificationRepository;
  private final EntityManager entityManager;

  @Override
  public Notification deleteNotification(long notificationId) {
    Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new GeneralHandler(ErrorStatus.NOTIFICATION_NOT_FOUND));
    notificationRepository.deleteById(notificationId);
    return notification;
  }

  @Override
  @Transactional
  public Notification updateNotification(long notificationId) {
    Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new GeneralHandler(ErrorStatus.NOTIFICATION_NOT_FOUND));
    notification.setIsRead(true);
    return notification;
  }

  @Override
  @Transactional
  public NotificationEventResponseDTO.NotificationEventDTO saveNotification(NotificationEventRequestDTO.NotificationEventDTO notificationEvent) {
    Member sender = memberRepository.findById(notificationEvent.getSenderId()).orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
    Member receiver = memberRepository.findById(notificationEvent.getReceiverId()).orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

    Notification notification = NotificationConverter.notificationEventDTO(sender, receiver, notificationEvent);
    notificationRepository.save(notification);
    return NotificationConverter.notificationEventDTO(receiver, notification);
  }

}
