package umc.lightup.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.config.EmailService;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberRepository;
import umc.lightup.school.converter.SchoolConverter;
import umc.lightup.school.domain.School;
import umc.lightup.school.domain.SchoolEmailVerification;
import umc.lightup.school.dto.SchoolEmailRequestDTO;
import umc.lightup.school.repository.SchoolEmailVerificationRepository;
import umc.lightup.school.repository.SchoolRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolQueryServiceImpl implements SchoolQueryService {

  private final EmailService emailService;
  private final SchoolRepository schoolRepository;
  private final SchoolEmailVerificationRepository schoolEmailVerificationRepository;
  private final MemberRepository memberRepository;

  @Override
  public Page<School> getSchoolList(String keyword, Integer page, Integer size) {
    if(keyword == null || keyword.trim().isEmpty()){
      return  schoolRepository.findAll(PageRequest.of(page, size));
    }
    return schoolRepository.findAllByNameContaining(keyword, PageRequest.of(page, size));
  }

  @Override
  @Transactional
  public void sendEmailVerification(Member member, Long schoolId, String email) {
    School school = schoolRepository.findById(schoolId).orElseThrow(() -> new GeneralHandler(ErrorStatus.SCHOOL_NOT_FOUND));

    // 이메일 도메인 확인 로직
    if(!isEmailDomainValid(email, school.getDomain())){
      throw new GeneralHandler(ErrorStatus.SCHOOL_DOMAIN_NOT_FOUND);
    }

    schoolEmailVerificationRepository.findByEmail(email).ifPresent(existing -> {
      if(!existing.isVerified() && existing.getExpiredAt().isAfter(LocalDateTime.now())){
        throw new GeneralHandler(ErrorStatus.SCHOOL_EMAIL_ALREADY_IN_PROGRESS);
      } else if(existing.isVerified()){
        throw new GeneralHandler(ErrorStatus.SCHOOL_EMAIL_ALREADY_VERIFIED);
      }
    });
    // 랜덤 코드 생성 로직
    String randomCode = generateRandomCode(6);

    //인증코드 유효기간 설정
    LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);

    SchoolEmailRequestDTO.SchoolEmailDTO schoolEmailDTO = SchoolEmailRequestDTO.SchoolEmailDTO.builder()
            .userName(member.getName())
            .code(randomCode)
            .build();
    emailService.sendEmailTemplate(email,
            "[Lightup] 이메일 인증 코드 안내",
            "email-verify",
            schoolEmailDTO);

    // 인증코드 및 이메일 저장 함수
    SchoolEmailVerification schoolEmailVerification = SchoolConverter.toEntity(member, school, email, randomCode, expiredAt);
    schoolEmailVerificationRepository.save(schoolEmailVerification);
  }

  @Override
  public void verifyEmail(String email, String code) {
    SchoolEmailVerification schoolEmailVerification = schoolEmailVerificationRepository.findByEmail(email).orElseThrow(
            () -> new GeneralHandler(ErrorStatus.SCHOOL_EMAIL_NOT_FOUND)
    );

    // 인증완료 처리 로직
    if(schoolEmailVerification.getExpiredAt().isBefore(LocalDateTime.now())){
      throw new GeneralHandler(ErrorStatus.SCHOOL_EMAIL_EXPIRED);
    }

    // 인증코드 비교 로직
    if(!schoolEmailVerification.getVerificationCode().equals(code)){
      throw new GeneralHandler(ErrorStatus.SCHOOL_EMAIL_NOT_CORRECT);
    }

    Member member = schoolEmailVerification.getMember();
    School school = schoolEmailVerification.getSchool();

    member.setSchool(school);
    schoolEmailVerification.setVerified(true);
    schoolEmailVerificationRepository.save(schoolEmailVerification);
  }


  // 인증코드 생성 함수
  public final String generateRandomCode(int length) {
    String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      int index = random.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }

    return sb.toString();
  }

  // 도메인 확인 함수
  private boolean isEmailDomainValid(String email, String expectedDomain) {
    if (email == null || !email.contains("@") || expectedDomain == null || expectedDomain.isBlank()) {
      return false;
    }
    String actualDomain = email.substring(email.lastIndexOf("@") + 1).toLowerCase().trim();
    return actualDomain.equalsIgnoreCase(expectedDomain.trim().toLowerCase());
  }
}
