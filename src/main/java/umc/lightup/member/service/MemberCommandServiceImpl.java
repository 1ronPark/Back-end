package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.aws.s3.AmazonS3Manager;
import umc.lightup.common.Uuid;
import umc.lightup.common.repository.UuidRepository;
import umc.lightup.config.JwtTokenProvider;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.*;
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

import java.util.ArrayList;
import java.util.Collections;

import umc.lightup.member.repository.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
    private final CredentialRepository credentialRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final MemberSkillRepository memberSkillRepository;
    private final MemberStrengthRepository memberStrengthRepository;
    private final MemberRegionRepository memberRegionRepository;
    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;
    private final StrengthRepository strengthRepository;
    private final PortfolioRepository portfolioRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final ActivityRepository activityRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AmazonS3Manager amazonS3Manager;
    private final UuidRepository uuidRepository;


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
        List<String> positions = memberPositionRepository.findPositionNameByMember(member);
        List<MemberRegion> regions = memberRegionRepository.findByMember(member);
        List<Portfolio> portfolios = portfolioRepository.findByMember(member);
        List<Activity> activities = activityRepository.findByMember(member);
        return MemberViewInfo.builder()
                .member(member)
                .skillNames(skills)
                .strengthNames(strengths)
                .regions(regions)
                .positionNames(positions)
                .portfolios(portfolios)
                .activities(activities)
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
    public MemberResponseDTO.MyProfileDTO getMemberProfile(Member member) {
        return MemberViewInfo.builder()
                .member(member)
                .skills(memberSkillRepository.findSkillByMember(member))
                .strengths(memberStrengthRepository.findStrengthByMember(member))
                .regions(memberRegionRepository.findByMember(member))
                .positionNames(memberPositionRepository.findPositionNameByMember(member))
                .portfolios(portfolioRepository.findByMember(member))
                .activities(activityRepository.findByMember(member))
                .emailOpen(true)
                .phoneOpen(true)
                .pictureOpen(true)
                .build().toMyProfileDTO();
    }

    @Override
    @Transactional
    public MemberResponseDTO.MyProfileDTO putMemberProfile(Member member, MemberRequestDTO.ProfileChangeDto request) {
        member.setSelfIntroduce(request.getSelfIntroduction());
        member.setProfileTitle(request.getProfileTitle());
        activityRepository.removeAllByMember(member);
        memberRepository.save(member);
        List<Activity> activities = activityRepository.saveAll(request.getActivities().stream()
                .map(a -> Activity.builder()
                        .member(member)
                        .name(a.getName())
                        .startDate(a.getStartDate())
                        .endDate(a.getEndDate())
                        .build())
                .toList());

        return MemberViewInfo.builder()
                .member(member)
                .skills(memberSkillRepository.findSkillByMember(member))
                .strengths(memberStrengthRepository.findStrengthByMember(member))
                .regions(memberRegionRepository.findByMember(member))
                .positionNames(memberPositionRepository.findPositionNameByMember(member))
                .portfolios(portfolioRepository.findByMember(member))
                .activities(activities)
                .emailOpen(true)
                .phoneOpen(true)
                .pictureOpen(true)
                .build().toMyProfileDTO();
    }

    @Override
    @Transactional
    public String saveMemberProfileImage(Member member, MultipartFile profileImage) {
        //삭제가 가능하면 관련 로직 시행
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        String generatedName = amazonS3Manager.generateProfileImageKeyName(savedUuid);
        String profileImageUrl = amazonS3Manager.uploadFile(generatedName, profileImage);
        member.setProfileImageUrl(profileImageUrl);
        memberRepository.save(member);
        return profileImageUrl;
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
    public List<MemberResponseDTO.singleRegionResultDTO> selectRegions(Member member, MemberRequestDTO.MemberRegionListRequestDTO request) {
        List<MemberResponseDTO.singleRegionResultDTO> resultDTOList = new ArrayList<>();
        for (MemberRequestDTO.MemberRegionRequestDTO dto : request.getMemberRegions()) {
            MemberRegion memberRegion = MemberRegion.builder()
                    .member(member)
                    .siDo(dto.getSiDo())
                    .siGunGu(dto.getSiGunGu() == null ? "전체" : dto.getSiGunGu())
                    .build();

            member.addMemberRegion(memberRegion);
            resultDTOList.add(MemberConverter.toSingleRegionResultDTO(memberRegion));
        }
        return resultDTOList;
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