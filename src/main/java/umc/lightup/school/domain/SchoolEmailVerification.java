package umc.lightup.school.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.lightup.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SchoolEmailVerification {
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String verificationCode;

  @Column(nullable = false)
  private boolean isVerified;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id")
  private School school;
}
