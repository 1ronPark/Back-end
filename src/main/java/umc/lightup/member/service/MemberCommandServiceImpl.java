package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.*;
import umc.lightup.member.dto.*;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.enums.Role;
import umc.lightup.member.repository.CredentialRepository;
import umc.lightup.member.repository.MemberPositionRepository;
import umc.lightup.member.repository.MemberRepository;
import umc.lightup.member.repository.MemberSkillRepository;
import umc.lightup.member.repository.MemberStrengthRepository;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.repository.SkillRepository;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.repository.StrengthRepository;
import umc.lightup.position.domain.Position;
import umc.lightup.position.repository.PositionRepository;

import umc.lightup.member.repository.*;
import umc.lightup.region.domain.Region;
import umc.lightup.region.repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
    private final CredentialRepository credentialRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final MemberSkillRepository memberSkillRepository;
    private final MemberStrengthRepository memberStrengthRepository;
    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;
    private final StrengthRepository strengthRepository;
    private final RegionRepository regionRepository;
    private final PortfolioRepository portfolioRepository;
    private final MemberLikeRepository memberLikeRepository;

    private final PasswordEncoder passwordEncoder;
    private final CredentialQueryService credentialQueryService;


    @Override
    @Transactional
    public Member joinMember(MemberRequestDTO.JoinDto request) {
        Credential credential = request.toCredential(passwordEncoder);
        Member member = credential.getMember();
        Member saved = memberRepository.save(member);
        credentialRepository.save(credential);
        return saved;
    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request) {
        Credential credential = credentialRepository
                .findByCredentialTypeAndEmail(CredentialType.PASSWORD, request.getEmail())
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), credential.getCredential())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }

        //주객전도가 되긴 했으나 한 번의 쿼리로 데이터를 가져오기 위한 가장 편한 방법임
        return credentialQueryService.getLoginResultDTO(credential.getMember());
    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginMemberByGoogle(String authCode) {
        OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo = credentialQueryService.getGoogleUserinfo(authCode);

        Credential credential = credentialRepository
                .findByCredentialTypeAndCredential(CredentialType.GOOGLE, userinfo.getSub())
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        return credentialQueryService.getLoginResultDTO(credential.getMember());
    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginMemberByKakao(String authCode) {
        OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo = credentialQueryService.getKakaoUserinfo(authCode);

        Credential credential = credentialRepository
                .findByCredentialTypeAndCredential(CredentialType.KAKAO, Long.toString(userinfo.getId()))
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        return credentialQueryService.getLoginResultDTO(credential.getMember());
    }

    @Override
    @Transactional
    public Member joinMemberByGoogle(String authCode) {
        OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo = credentialQueryService.getGoogleUserinfo(authCode);

        if (isEmailExist(userinfo.getEmail()))
            throw new GeneralHandler(ErrorStatus.ALREADY_SIGNED_IN_EMAIL);

        Member member = Member.builder()
                .email(userinfo.getEmail())
                .name(userinfo.getName())
                .role(Role.PROVISION)
                .build();
        Credential credential = Credential.builder()
                .credentialType(CredentialType.GOOGLE)
                .member(member)
                .credential(userinfo.getSub())
                .build();
        Member saved = memberRepository.save(member);
        credentialRepository.save(credential);
        return saved;
    }

    @Override
    @Transactional
    public Member joinMemberByKakao(String authCode) {
        OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo = credentialQueryService.getKakaoUserinfo(authCode);

        if (userinfo.getEmail() == null || isEmailExist(userinfo.getEmail()))
            throw new GeneralHandler(ErrorStatus.ALREADY_SIGNED_IN_EMAIL);

        Member member = Member.builder()
                .email(userinfo.getEmail())
                .name(userinfo.getKakao_account().getProfile().getNickname())
                .profileImageUrl(userinfo.getKakao_account().getProfile().getProfile_image_url())
                .role(Role.PROVISION)
                .build();
        Credential credential = Credential.builder()
                .credentialType(CredentialType.KAKAO)
                .member(member)
                .credential(Long.toString(userinfo.getId()))
                .build();
        Member saved = memberRepository.save(member);
        credentialRepository.save(credential);
        return saved;
    }

    @Override
    @Transactional
    public Member addGoogleLogin(Member member, String authCode) {
        if (credentialRepository.existsByCredentialTypeAndMember(CredentialType.GOOGLE, member))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_EXIST);
        OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo = credentialQueryService.getGoogleUserinfo(authCode);
        if (credentialRepository.existsByCredentialTypeAndCredential(CredentialType.GOOGLE, userinfo.getSub()))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_USED);

        Credential credential = Credential.builder()
                .credentialType(CredentialType.GOOGLE)
                .member(member)
                .credential(userinfo.getSub())
                .build();
        credentialRepository.save(credential);
        return member;
    }

    @Override
    @Transactional
    public Member addKakaoLogin(Member member, String authCode) {
        if (credentialRepository.existsByCredentialTypeAndMember(CredentialType.KAKAO, member))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_EXIST);
        OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo = credentialQueryService.getKakaoUserinfo(authCode);
        if (credentialRepository.existsByCredentialTypeAndCredential(CredentialType.KAKAO, Long.toString(userinfo.getId())))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_USED);

        Credential credential = Credential.builder()
                .credentialType(CredentialType.KAKAO)
                .member(member)
                .credential(Long.toString(userinfo.getId()))
                .build();
        credentialRepository.save(credential);
        return member;
    }

    @Override
    @Transactional
    public Member addPasswordLogin(Member member, String password) {
        if (credentialRepository.existsByCredentialTypeAndMember(CredentialType.PASSWORD, member))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_EXIST);
        Credential credential = Credential.builder()
                .credentialType(CredentialType.PASSWORD)
                .member(member)
                .credential(passwordEncoder.encode(password))
                .build();
        credentialRepository.save(credential);
        return member;
    }

    @Override
    @Transactional
    public void removeCredential(Member member, CredentialType credentialType) {
        if (credentialRepository.countByMember(member) <= 1)
            throw new GeneralHandler(ErrorStatus.ONLY_CREDENTIAL_REMAIN);

        if (credentialRepository.removeByCredentialTypeAndMember(credentialType, member) == 0)
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND);
    }

    @Override
    public Member getMember(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void selectPosition(Long memberId, String positionName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Position position = positionRepository.findByName(positionName)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POSITION_NOT_FOUND));

        if (memberPositionRepository.existsByMemberIdAndPositionId(memberId, position.getId())){
            throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }

        memberPositionRepository.save(new MemberPosition(member, position));
    }

    @Override
    @Transactional
    public void deletePosition(Long memberId, String positionName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Position position = positionRepository.findByName(positionName)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POSITION_NOT_FOUND));

        MemberPosition memberPosition = memberPositionRepository.findByMemberIdAndPositionId(memberId, position.getId())
                .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        memberPositionRepository.delete(memberPosition);
    }

    @Override
    public MemberResponseDTO.MemberInfoDTO getMember(long id, String viewerEmail){
        // 한 번에 반환하기 위해 DTO 반환을 사용했는데 좋은 설계 방식인지는 한번 고민할 필요가 있음
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<String> skills = memberSkillRepository.findSkillNameByMember(member);
        List<String> strengths = memberStrengthRepository.findStrengthNameByMember(member);
        List<Region> regions = regionRepository.findByMember(member);
        List<Portfolio> portfolios = portfolioRepository.findByMember(member);
        return MemberViewInfo.builder()
                .member(member)
                .skills(skills)
                .strengths(strengths)
                .regions(regions)
                .portfolios(portfolios)
                .emailOpen(false)
                .phoneOpen(false)
                .pictureOpen(false)
                .build().toMemberInfoDTO();
    }

    @Override
    @Transactional
    public Member putMember(String email, MemberRequestDTO.ChangeDto request) {
        Member member = getMember(email);
        if (!request.getEmail().equals(member.getEmail()) &&
                isEmailExist(request.getEmail()))
            throw new GeneralHandler(ErrorStatus.DUPLICATE_EMAIL);
        if (!request.getPhoneNumber().equals(member.getPhoneNumber()) &&
                isPhoneNumberExist(request.getPhoneNumber()))
            throw new GeneralHandler(ErrorStatus.DUPLICATE_PHONE_NUMBER);
        if (member.getNickname() != null &&
                !member.getNickname().equals(request.getNickname()) &&
                isNicknameExist(request.getNickname()))
            throw new GeneralHandler(ErrorStatus.DUPLICATE_NICKNAME);
        return memberRepository.save(request.toMember(member.getId()));
    }
  
    @Override
    @Transactional
    public String selectSkill(Long skillId, Member member) {
        Skill foundSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.SKILL_NOT_FOUND));
        MemberSkill memberSkill = MemberSkill.createMemberSkill(member, foundSkill);

        if (memberSkillRepository.existsByMemberAndSkill(member, foundSkill)) {
            throw new GeneralHandler(ErrorStatus.DUPLICATED_SKILL_SELECT);
        }
        memberSkillRepository.save(memberSkill);

        return foundSkill.getName();
    }

    @Override
    @Transactional
    public String selectStrength(Long strengthId, Member member) {
        Strength foundStrength = strengthRepository.findById(strengthId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.STRENGTH_NOT_FOUND));
        MemberStrength memberStrength = MemberStrength.createMemberStrength(member, foundStrength);

        if (memberStrengthRepository.existsByMemberAndStrength(member, foundStrength)) {
            throw new GeneralHandler(ErrorStatus.DUPLICATED_STRENGTH_SELECT);
        }
        memberStrengthRepository.save(memberStrength);

        return foundStrength.getName();
    }

    @Override
    @Transactional
    public void addMemberLike(Member fromMember, long toMemberId) {
        if (fromMember.getId() == toMemberId)
            throw new GeneralHandler(ErrorStatus.SELF_LIKE);
        Member toMember = memberRepository.findById(toMemberId) //아 괜히 검색쿼리 더 날리고 싶지 않은데
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (memberLikeRepository.existsByFromMemberIdAndToMemberId(fromMember.getId(), toMemberId))
            throw new GeneralHandler(ErrorStatus.ALREADY_LIKED);
        memberLikeRepository.save(MemberLike.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build());
    }

    @Override
    @Transactional
    public void removeMemberLike(String fromMemberEmail, long toMemberId) {
        if (memberLikeRepository.removeByFromMemberEmailAndToMemberId(fromMemberEmail, toMemberId) == 0)
            //데이터를 지우면서 지운 row의 수가 0은 아닌지 확인(1이어야 함)
            throw new GeneralHandler(ErrorStatus.LIKE_NOT_FOUND);
    }

    @Override
    public boolean isNicknameExist(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public boolean isEmailExist(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneNumberExist(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }
}