package umc.lightup.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.*;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.Mbti;
import umc.lightup.position.domain.QPosition;
import umc.lightup.region.domain.QRegion;
import umc.lightup.skill.domain.QSkill;
import umc.lightup.strength.domain.QStrength;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final QMember member = QMember.member;
    private final QMemberPosition memberPosition = QMemberPosition.memberPosition;
    private final QPosition position = QPosition.position;
    private final QMemberRegion memberRegion = QMemberRegion.memberRegion;
    private final QRegion region = QRegion.region;
    private final QMemberLike memberLike = QMemberLike.memberLike;
    private final QSkill skill = QSkill.skill;
    private final QMemberSkill memberSkill = QMemberSkill.memberSkill;
    private final QStrength strength = QStrength.strength;
    private final QMemberStrength memberStrength = QMemberStrength.memberStrength;

    @Override
    public MemberResponseDTO.MemberInfoListDTO getMemberInfos
            (Member requestedMember, MemberRequestDTO.MemberSearchRequestDTO options) {
        // 아니 JSON_OBJECTAGG 함수로 일대다들을 한 번에 묶어 가져올 생각 하고 있었는데 QueryDSL이 지원을 안 한다네요???
        // 그 함수가 MySQL에만 있고 다른 DB에는 없다니 이해는 하는데, 그렇다고 Native Query를 쓰기는 많이 복잡할 것 같은데...
        BooleanBuilder predicate = new BooleanBuilder();

        // 1) Position.name 리스트 필터
        if (options.getPositions() != null && !options.getPositions().isEmpty()) {
            BooleanExpression positionCondition = JPAExpressions
                    .selectOne()
                    .from(memberPosition)
                    .join(memberPosition.position, position)
                    .where(
                            memberPosition.member.eq(member),
                            position.name.in(options.getPositions())
                    )
                    .exists();

            // 메인 쿼리에서 predicate에 추가
            predicate.and(positionCondition);
        }

        // 2) Region 필터
        if (options.getRegions() != null && !options.getRegions().isEmpty()) {
            BooleanBuilder regionBuilder = new BooleanBuilder();
            for (MemberRequestDTO.MemberRegionRequestDTO cond : options.getRegions()) {
                if (cond.getSiGunGu() != null)
                    regionBuilder.or(
                            JPAExpressions
                                    .selectOne()
                                    .from(memberRegion)
                                    .where(
                                            memberRegion.member.eq(member),
                                            memberRegion.siDo.eq(cond.getSiDo()),
                                            memberRegion.siGunGu.eq(cond.getSiGunGu())
                                    )
                                    .exists()
                    );
                else regionBuilder.or(
                        JPAExpressions
                                .selectOne()
                                .from(memberRegion)
                                .where(
                                        memberRegion.member.eq(member),
                                        memberRegion.siDo.eq(cond.getSiDo())
                                )
                                .exists()
                );
            }
            predicate.and(regionBuilder);
        }

        // 3) MBTI 비트마스크 필터
        if (options.getMbtiE() != null) {
            if (options.getMbtiE()) predicate.and(memberMbtiBitAnd(8).ne(0));
            else predicate.and(memberMbtiBitAnd(8).eq(0));
        }
        if (options.getMbtiN() != null) {
            if (options.getMbtiN()) predicate.and(memberMbtiBitAnd(4).ne(0));
            else predicate.and(memberMbtiBitAnd(4).eq(0));
        }
        if (options.getMbtiF() != null) {
            if (options.getMbtiF()) predicate.and(memberMbtiBitAnd(2).ne(0));
            else predicate.and(memberMbtiBitAnd(2).eq(0));
        }
        if (options.getMbtiP() != null) {
            if (options.getMbtiP()) predicate.and(memberMbtiBitAnd(1).ne(0));
            else predicate.and(memberMbtiBitAnd(1).eq(0));
        }

        // 4) 좋아요
        if (requestedMember != null && options.getOnlyLiked() != null && options.getOnlyLiked()) {
            BooleanExpression likedExists = JPAExpressions
                    .selectOne()
                    .from(memberLike)
                    .where(
                            memberLike.toMember.eq(member),         // 이 member가 좋아요 받은 대상
                            memberLike.fromMember.eq(requestedMember) // 내가 누른 '좋아요' 인가
                    )
                    .exists();
            predicate.and(likedExists);
        }

        if (options.getPage() == null) options.setPage(1L);

        BooleanExpression likedCondition;
        if (requestedMember == null) likedCondition = Expressions.FALSE;
        else likedCondition = JPAExpressions
                    .selectOne()
                    .from(memberLike)
                    .where(
                            memberLike.toMember.eq(member),          // 조회 중인 멤버가 좋아요 받은 대상
                            memberLike.fromMember.eq(requestedMember) // 조회자 기준
                    )
                    .exists();


        List<MemberWithLikeDTO> memberList = jpaQueryFactory
                .select(Projections.constructor(MemberWithLikeDTO.class,
                        member.id,
                        member.name,
                        member.nickname,
                        member.gender,
                        member.mbti,
                        member.profileImageUrl,
                        likedCondition.as("liked")))
                .from(member)
                .where(predicate)
                .offset((options.getPage() - 1) * 16)
                .limit(16)
                .fetch();

        List<MemberResponseDTO.MemberInfoSimpleDTO> resultList = memberList.stream()
                .map(m -> MemberResponseDTO.MemberInfoSimpleDTO.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .nickname(m.getNickname())
                        .gender(m.getGender())
                        .mbti(Mbti.fromByte(m.getMbti()))
                        .profileImageUrl(m.getProfileImageUrl())
                        .liked(m.isLiked())
                        .positions(List.of())
                        .regions(List.of())
                        .skills(List.of())
                        .strengths(List.of())
                        .build())
                .toList();

        //이후 id값을 기준으로 데이터 넣을 때 활용, list로 선형탐색하면 시간복잡도가 증가하기 때문에 쓴 방식
        Map<Long, MemberResponseDTO.MemberInfoSimpleDTO> mapById =
                resultList.stream().collect(Collectors.toMap(
                MemberResponseDTO.MemberInfoSimpleDTO::getId,
                m -> m));

        List<Long> memberIds = memberList.stream()
                .map(MemberWithLikeDTO::getId)
                .toList();

        //positions
        jpaQueryFactory
                .select(Projections.constructor(MemberIdAndNameDTO.class,
                        memberPosition.member.id,
                        position.name))
                .from(memberPosition)
                .join(memberPosition.position, position)
                .where(memberPosition.member.id.in(memberIds))
                .fetch().stream()
                .collect(Collectors.groupingBy(
                        MemberIdAndNameDTO::getId,
                        Collectors.mapping(MemberIdAndNameDTO::getName, Collectors.toList())))
                .forEach((key, value) -> mapById.get(key).setPositions(value));

        //regions
        jpaQueryFactory
                .select(Projections.constructor(MemberIdAndRegionDTO.class,
                        memberRegion.member.id,
                        memberRegion.siDo,
                        memberRegion.siGunGu))
                .from(memberRegion)
                .where(memberRegion.member.id.in(memberIds))
                .fetch().stream()
                .collect(Collectors.groupingBy(
                        MemberIdAndRegionDTO::getId,
                        Collectors.mapping(r->
                                MemberResponseDTO.singleRegionResultDTO.builder()
                                        .siDo(r.siDo)
                                        .siGunGu(r.siGunGu)
                                        .build(), Collectors.toList())))
                .forEach((key, value) -> mapById.get(key).setRegions(value));

        //skills
        jpaQueryFactory
                .select(Projections.constructor(MemberIdAndNameDTO.class,
                        memberSkill.member.id,
                        skill.name))
                .from(memberSkill)
                .join(memberSkill.skill, skill)
                .where(memberSkill.member.id.in(memberIds))
                .fetch().stream()
                .collect(Collectors.groupingBy(
                        MemberIdAndNameDTO::getId,
                        Collectors.mapping(MemberIdAndNameDTO::getName, Collectors.toList())))
                .forEach((key, value) -> mapById.get(key).setSkills(value));

        //strengths
        jpaQueryFactory
                .select(Projections.constructor(MemberIdAndNameDTO.class,
                        memberStrength.member.id,
                        strength.name))
                .from(memberStrength)
                .join(memberStrength.strength, strength)
                .where(memberStrength.member.id.in(memberIds))
                .fetch().stream()
                .collect(Collectors.groupingBy(
                        MemberIdAndNameDTO::getId,
                        Collectors.mapping(MemberIdAndNameDTO::getName, Collectors.toList())))
                .forEach((key, value) -> mapById.get(key).setStrengths(value));

        long totalResultCount = Optional.ofNullable(jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(predicate)
                .fetchOne()).orElse(0L);

        return MemberResponseDTO.MemberInfoListDTO.builder()
                .members(resultList)
                .numOfTotalResults(totalResultCount)
                .build();
    }

    @Getter
    @AllArgsConstructor
    //private 사용 불가
    public static class MemberWithLikeDTO {
        private long id;
        private String name;
        private String nickname;
        private Boolean gender;
        private Byte mbti;
        private String profileImageUrl;
        private boolean liked;
    }

    @Getter
    @AllArgsConstructor
    //private 사용 불가
    public static class MemberIdAndNameDTO {
        private long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    //private 사용 불가
    public static class MemberIdAndRegionDTO {
        private long id;
        private String siDo;
        private String siGunGu;
    }

    private NumberTemplate<Integer> memberMbtiBitAnd(int mask) {
        return Expressions.numberTemplate(Integer.class, "function('bitand', {0}, {1})", member.mbti, mask);
    }
}