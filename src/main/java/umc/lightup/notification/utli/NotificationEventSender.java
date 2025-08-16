package umc.lightup.notification.utli;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import umc.lightup.notification.dto.NotificationEventRequestDTO;
import umc.lightup.notification.enums.NotificationType;
import umc.lightup.notification.enums.ReferenceType;

@Component
@RequiredArgsConstructor
public class NotificationEventSender {
  private final ApplicationEventPublisher publisher;

  public void send(Long senderId, Long receiverId, NotificationType type, String message, ReferenceType referenceType, Long referenceId){
    NotificationEventRequestDTO.NotificationEventDTO event = new NotificationEventRequestDTO.NotificationEventDTO(
            senderId, receiverId, type, message, referenceType, referenceId
    );
    send(event);
  }

  public void send(NotificationEventRequestDTO.NotificationEventDTO event) {
    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      // 트랜잭션 존재 시, AFTER_COMMIT 이후 발행
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          publisher.publishEvent(event);
        }
      });
    } else {
      // 트랜잭션이 없으면 즉시 발행
      publisher.publishEvent(event);
    }
  }
}