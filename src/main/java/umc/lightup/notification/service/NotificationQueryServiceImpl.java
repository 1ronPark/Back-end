package umc.lightup.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRepository;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.repository.NotificationRepostiory;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

  private final MemberRepository memberRepository;
  private final NotificationRepostiory notificationRepostiory;

  @Override
  public Page<Notification> getNotificationList(Member member, Integer page, Integer size) {
    Page<Notification> NotificationPage = notificationRepostiory.findAllByReceiver(member, PageRequest.of(page, size));
    return NotificationPage;
  }
}
