package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.aws.s3.AmazonS3Manager;
import umc.lightup.common.Uuid;
import umc.lightup.common.repository.UuidRepository;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.*;
import umc.lightup.member.dto.*;
import umc.lightup.member.repository.*;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.repository.SkillRepository;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.repository.StrengthRepository;
import umc.lightup.position.domain.Position;
import umc.lightup.position.repository.PositionRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
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
    private final MemberViewHistoryRepository memberViewHistoryRepository;

    private final AmazonS3Manager amazonS3Manager;
    private final UuidRepository uuidRepository;

    @Override
    public Member getMember(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public Member getMemberWithSchool(String email){
        return memberRepository.findByEmailJoinFetchSchool(email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void selectPosition(Long memberId, String positionName) {
        Member member = getMember(memberId);
        Position position = positionRepository.findByName(positionName)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POSITION_NOT_FOUND));

        if (memberPositionRepository.existsByMemberIdAndPositionId(memberId, position.getId())){
            throw new GeneralHandler(ErrorStatus.DUPLICATED_POSITION_SELECT);
        }

        memberPositionRepository.save(new MemberPosition(member, position));
    }

    @Override
    @Transactional
    public void deletePosition(Long memberId, String positionName) {
        Member member = getMember(memberId);
        Position position = positionRepository.findByName(positionName)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POSITION_NOT_FOUND));

        MemberPosition memberPosition = memberPositionRepository.findByMemberIdAndPositionId(memberId, position.getId())
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_POSITION_NOT_FOUND));

        memberPositionRepository.delete(memberPosition);
    }

    @Override
    @Transactional
    public MemberResponseDTO.MemberInfoDTO getMember(long id, Member requestedMember) {
//        // 한 번에 반환하기 위해 DTO 반환을 사용했는데 좋은 설계 방식인지는 한번 고민할 필요가 있음
//        Member member = getMember(id);
//        List<String> skills = memberSkillRepository.findSkillNameByMember(member);
//        List<String> strengths = memberStrengthRepository.findStrengthNameByMember(member);
//        List<String> positions = memberPositionRepository.findPositionNameByMember(member);
//        List<MemberRegion> regions = memberRegionRepository.findByMember(member);
//        List<Portfolio> portfolios = portfolioRepository.findByMember(member);
//        List<Activity> activities = activityRepository.findByMember(member);

        MemberResponseDTO.MemberInfoDTO memberInfo =
                memberRepository.getSingleMemberInfo(requestedMember, id);

        //조회기록 설정
        if (requestedMember != null) {
            long toMemberId = memberInfo.getId();
            if (!requestedMember.getId().equals(toMemberId)) {
                int updatedResult = memberViewHistoryRepository.updateTimestamp(requestedMember, toMemberId);
                if (updatedResult == 0) { // update 내역이 없을 때
                    MemberViewHistory history = MemberViewHistory.builder()
                            .fromMember(requestedMember)
                            .toMember(getMember(toMemberId)) //아 그냥 FK만 가지고 만들면 안되나?
                            .build();
                    memberViewHistoryRepository.save(history);
                }
            }
        }

        return memberInfo;

//        return MemberViewInfo.builder()
//                .member(member)
//                .skillNames(skills)
//                .strengthNames(strengths)
//                .regions(regions)
//                .positionNames(positions)
//                .portfolios(portfolios)
//                .activities(activities)
//                .emailOpen(true)
//                .phoneOpen(true)
//                .pictureOpen(true)
//                .build().toMemberInfoDTO();
    }

    @Override
    public List<MemberResponseDTO.HistoryInfoDTO> getHistory(Member member, long size) {
        return memberRepository.getMemberViewHistoryInfos(member, size);
    }

    @Override
    @Transactional
    public Member putMember(String email, MemberRequestDTO.ChangeDto request) {
        Member member = getMemberWithSchool(email);
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
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setGender(request.getGender());
        member.setBirth(request.getBirth());
        member.setAge((int) request.getBirth().until(LocalDate.now(), ChronoUnit.YEARS));
        member.setRole(request.getRole());
        member.setMbti(request.getMbti().toByte());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setProfileTitle(request.getProfileTitle());
        return member; //dirty checking 이용
    }

    @Override
    @Transactional
    public Member putMemberBasic(String email, MemberRequestDTO.BasicChangeDto request) {
        Member member = getMemberWithSchool(email);
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
        member.setNickname(request.getNickname());
        member.setMbti(request.getMbti().toByte());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setProfileTitle(request.getProfileTitle());
        return member; //dirty checking 이용
    }

    @Override
    public MemberResponseDTO.MyProfileDTO getMemberProfile(String email) {
        Member member = memberRepository.findByEmailJoinFetchSchool(email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
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
    public MemberResponseDTO.MyProfileDTO putMemberProfile(String email, MemberRequestDTO.ProfileChangeDto request) {
        Member member = memberRepository.findByEmailJoinFetchSchool(email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setSelfIntroduce(request.getSelfIntroduction());
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
        //삭제가 가능하면 진행하는 게 좋긴 함
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
    public Portfolio savePortfolio(Member member, String name, MultipartFile portfolioFile) {
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        String generatedName = amazonS3Manager.generatePortFolioKeyName(savedUuid);
        String profileImageUrl = amazonS3Manager.uploadFile(generatedName, portfolioFile);
        return savePortfolio(member, name, profileImageUrl);
    }

    @Override
    @Transactional
    public Portfolio savePortfolio(Member member, String name, String portfolioLink) {
        Portfolio portfolio = Portfolio.builder()
                .name(name)
                .fileUrl(portfolioLink)
                .member(member)
                .build();
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void removePortfolio(String memberEmail, long portFolioId) {
        //삭제가 가능하면 진행하는 게 좋긴 함
        if (portfolioRepository.removeByIdAndMemberEmail(portFolioId, memberEmail) == 0)
            //데이터를 지우면서 지운 row의 수가 0은 아닌지 확인(1이어야 함)
            throw new GeneralHandler(ErrorStatus.PORTFOLIO_NOT_FOUND);
    }

    @Override
    public MemberResponseDTO.MemberInfoListDTO searchMember(
            Member member,
            MemberRequestDTO.MemberSearchRequestDTO options) {
        if (options.getRegions() == null) options.setRegions(List.of());
        if (options.getPositions() == null) options.setPositions(List.of());
        return memberRepository.getMemberInfos(member, options);
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
    public void removeMemberSkill(Long skillId, Long memberId) {
        if (memberSkillRepository.deleteByMemberIdAndSkillId(memberId, skillId) == 0)
            throw new GeneralHandler(ErrorStatus.MEMBER_SKILL_NOT_FOUND);
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
    public void removeMemberStrength(Long strengthId, Long memberId) {
        if (memberStrengthRepository.deleteByMemberIdAndStrengthId(memberId, strengthId) == 0)
            throw new GeneralHandler(ErrorStatus.MEMBER_STRENGTH_NOT_FOUND);
    }

    @Override
    @Transactional
    public List<MemberResponseDTO.singleRegionResultDTO> selectRegions(Member member, MemberRequestDTO.MemberRegionListRequestDTO request) {
        member = getMember(member.getId());
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
    public void removeMemberRegion(Long memberRegionId, Long memberId) {
        if (memberRegionRepository.deleteByIdAndMemberId(memberRegionId, memberId) == 0)
            throw new GeneralHandler(ErrorStatus.MEMBER_REGION_NOT_FOUND);
    }

    @Override
    @Transactional
    public void addMemberLike(Member fromMember, long toMemberId) {
        if (fromMember.getId() == toMemberId)
            throw new GeneralHandler(ErrorStatus.SELF_LIKE);
        Member toMember = getMember(toMemberId); //아 괜히 검색쿼리 더 날리고 싶지 않은데
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