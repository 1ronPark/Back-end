package umc.lightup.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.member.domain.Member;
import umc.lightup.notification.domain.Notification;

public interface NotificationRepostiory extends JpaRepository<Notification, Long> {
  Page<Notification> findAllByReceiver(Member member, PageRequest pageRequest);
}
