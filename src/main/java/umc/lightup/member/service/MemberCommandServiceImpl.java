package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.config.JwtTokenProvider;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberSkill;
import umc.lightup.member.domain.MemberStrength;
import umc.lightup.member.domain.MemberPosition;
import umc.lightup.member.domain.Portfolio;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.dto.MemberViewInfo;
import umc.lightup.member.enums.CredentialType;
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

import java.util.Collections;

import umc.lightup.member.repository.*;
import umc.lightup.region.domain.Region;
import umc.lightup.region.repository.RegionRepository;
import umc.lightup.skill.repository.SkillRepository;
import umc.lightup.strength.repository.StrengthRepository;

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
  
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


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
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Member member = credential.getMember(); //주객전도가 되긴 했으나 한 번의 쿼리로 데이터를 가져오기 위한 가장 편한 방법임

        if(!passwordEncoder.matches(request.getPassword(), credential.getCredential())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(), null,
                Collections.singleton(() -> member.getRole().name())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);

        return MemberResponseDTO.loginResultDTOBuilder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .build();
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