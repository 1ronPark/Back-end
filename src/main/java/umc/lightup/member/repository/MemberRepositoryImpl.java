package umc.lightup.member.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.config.QueryDSLTemplate;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.*;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.Mbti;
import umc.lightup.position.domain.QPosition;
import umc.lightup.region.domain.QRegion;
import umc.lightup.skill.domain.QSkill;
import umc.lightup.strength.domain.QStrength;

import java.util.List;
import java.util.Optional;

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

    private final QueryDSLTemplate queryDSLTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public MemberResponseDTO.MemberInfoListDTO getMemberInfos
            (Member requestedMember, MemberRequestDTO.MemberSearchRequestDTO options) {
        // JSON_OBJECTAGG 구현 버전
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
        if (options.getLimit() == null) options.setLimit(16L);

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

        List<MemberResponseDTO.MemberInfoSimpleDTO> resultList = jpaQueryFactory
                .select(Projections.constructor(MemberWithLikeDTO.class,
                        member.id,
                        member.name,
                        member.nickname,
                        member.gender,
                        member.mbti,
                        member.profileImageUrl,

                        // Positions 집계
                        JPAExpressions
                                .select(queryDSLTemplate.jsonArrayAgg(position.name))
                                .from(memberPosition)
                                .join(memberPosition.position, position)
                                .where(memberPosition.member.id.eq(member.id)),

                        // Regions 집계
                        JPAExpressions
                                .select(queryDSLTemplate.jsonArrayAgg(
                                        queryDSLTemplate.jsonObject(
                                                Expressions.constant("siDo"), memberRegion.siDo,
                                                Expressions.constant("siGunGu"), memberRegion.siGunGu
                                        )
                                ))
                                .from(memberRegion)
                                .where(memberRegion.member.id.eq(member.id)),

                        // Skills 집계
                        JPAExpressions
                                .select(queryDSLTemplate.jsonArrayAgg(skill.name))
                                .from(memberSkill)
                                .join(memberSkill.skill, skill)
                                .where(memberSkill.member.id.eq(member.id)),

                        // Strengths 집계
                        JPAExpressions
                                .select(queryDSLTemplate.jsonArrayAgg(strength.name))
                                .from(memberStrength)
                                .join(memberStrength.strength, strength)
                                .where(memberStrength.member.id.eq(member.id)),
                        likedCondition.as("liked")))
                .from(member)
                .where(predicate)
                .offset((options.getPage() - 1) * options.getLimit())
                .limit(options.getLimit())
                .fetch().stream()
                .map(m -> m.toMemberInfoSimpleDTO(objectMapper))
                .toList();


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
        private String positionsJson;
        private String regionsJson;
        private String skillsJson;
        private String strengthsJson;
        private boolean liked;

        public MemberResponseDTO.MemberInfoSimpleDTO toMemberInfoSimpleDTO(ObjectMapper objectMapper) {
            try {
                //null이 가능하다 했으니 null check 진행
                List<String> positions;
                if (positionsJson == null || positionsJson.isBlank()) positions = List.of();
                else positions = objectMapper.readValue(positionsJson, new TypeReference<>(){});

                List<MemberResponseDTO.singleRegionResultDTO> regions;
                if (regionsJson == null || regionsJson.isBlank()) regions = List.of();
                else regions = objectMapper.readValue(regionsJson, new TypeReference<>(){});

                List<String> skills;
                if (skillsJson == null || skillsJson.isBlank()) skills = List.of();
                else skills = objectMapper.readValue(skillsJson, new TypeReference<>(){});

                List<String> strengths;
                if (strengthsJson == null || strengthsJson.isBlank()) strengths = List.of();
                else strengths = objectMapper.readValue(strengthsJson, new TypeReference<>(){});

                return MemberResponseDTO.MemberInfoSimpleDTO.builder()
                        .id(id)
                        .name(name)
                        .nickname(nickname)
                        .gender(gender)
                        .mbti(mbti == null ? null : Mbti.fromByte(mbti))
                        .profileImageUrl(profileImageUrl)
                        .positions(positions) // <> 내부 생략 가능
                        .regions(regions)
                        .skills(skills)
                        .strengths(strengths)
                        .liked(liked)
                        .build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new GeneralHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
            }
        }
    }

    private NumberTemplate<Integer> memberMbtiBitAnd(int mask) {
        return Expressions.numberTemplate(Integer.class, "function('bitand', {0}, {1})", member.mbti, mask);
    }
}