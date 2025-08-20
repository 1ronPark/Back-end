package umc.lightup.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.*;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.item.dto.ItemResponseDTO;
import umc.lightup.item.enums.CategoryType;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.QMember;
import umc.lightup.school.domain.QSchool;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final QItem item = QItem.item;
    private final QMember member = QMember.member;
    private final QItemCategory itemCategory = QItemCategory.itemCategory;
    private final QRecruitPosition recruitPosition = QRecruitPosition.recruitPosition;
    private final QItemRegion itemRegion = QItemRegion.itemRegion;
    private final QItemComment itemComment = QItemComment.itemComment;
    private final QItemLike itemLike = QItemLike.itemLike;
    private final QSchool school = QSchool.school;

    @Override
    public List<ItemResponseDTO.ItemResultDTO> searchItems(Member requestedMember,
                                                           Pageable pageable,
                                                           String category,
                                                           Long positionId,
                                                           ItemRequestDTO.ItemRegionSearchRequestDTO itemRegionDTOs,
                                                           Boolean onlyLiked,
                                                           String sort) {
        BooleanBuilder predicate = new BooleanBuilder();

        // 1) CategoryType 필터
        if (category != null) {
            CategoryType categoryType = CategoryType.toCategoryType(category);

            BooleanExpression categoryCondition = JPAExpressions
                    .selectOne()
                    .from(itemCategory)
                    .where(
                            itemCategory.item.eq(item),
                            itemCategory.categoryType.eq(categoryType)
                    )
                    .exists();

            predicate.and(categoryCondition);
        }

        // 2) Position 필터
        if (positionId != null) {
            BooleanExpression positionCondition = JPAExpressions
                    .selectOne()
                    .from(recruitPosition)
                    .where(
                            recruitPosition.item.eq(item),
                            recruitPosition.position.id.eq(positionId)
                    )
                    .exists();

            predicate.and(positionCondition);
        }

        // 3) Region 필터
        if (itemRegionDTOs.getItemRegions() != null && !itemRegionDTOs.getItemRegions().isEmpty()) {
            BooleanBuilder regionBuilder = new BooleanBuilder();
            for (ItemRequestDTO.CollaborationRegionRequestDTO cond : itemRegionDTOs.getItemRegions()) {
                if (cond.getSiGunGu() != null)
                    regionBuilder.or(
                            JPAExpressions
                                    .selectOne()
                                    .from(itemRegion)
                                    .where(
                                            itemRegion.item.eq(item),
                                            itemRegion.siDo.eq(cond.getSiDo()),
                                            itemRegion.siGunGu.eq(cond.getSiGunGu())
                                    )
                                    .exists()
                    );
                else regionBuilder.or(
                        JPAExpressions
                                .selectOne()
                                .from(itemRegion)
                                .where(
                                        itemRegion.item.eq(item),
                                        itemRegion.siDo.eq(cond.getSiDo())
                                )
                                .exists()
                );
            }
            predicate.and(regionBuilder);
        }

        // 4) ItemLike(좋아요) 필터
        if (requestedMember != null && onlyLiked != null && onlyLiked) {
            predicate.and(getLikedCondition(requestedMember));
        }

        BooleanExpression likedCondition;
        if (requestedMember == null) likedCondition = Expressions.FALSE;
        else likedCondition = getLikedCondition(requestedMember);

        OrderSpecifier<?> orderSpecifier;

        if ("popular".equals(sort)) {
            orderSpecifier = item.viewCount.desc();
        } else {
            orderSpecifier = item.createdAt.desc();
        }

        return jpaQueryFactory
                .select(Projections.constructor(ItemResponseDTO.ItemResultDTO.class,
                        item.id,
                        item.name,
                        school.name,
                        item.introduce,
                        member.name,
                        member.profileImageUrl,
                        item.itemProfileImageUrl,
                        item.updatedAt,
                        item.projectStatus,
                        item.viewCount,

                        // 댓글 개수
                        JPAExpressions
                                .select(itemComment.count())
                                .from(itemComment)
                                .where(itemComment.item.eq(item)),

                        // 좋아요 여부
                        likedCondition
                ))
                .from(item)
                .leftJoin(item.member, member)
                .leftJoin(member.school, school)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();
    }

    private BooleanExpression getLikedCondition(Member requestedMember) {
        return JPAExpressions
                .selectOne()
                .from(itemLike)
                .where(
                        itemLike.item.eq(item),
                        itemLike.member.eq(requestedMember)
                )
                .exists();
    }
}

