package umc.lightup.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public ItemResponseDTO.ItemInfoListDTO getItemInfos(Member currentMember,
                                                        ItemRequestDTO.ItemSearchRequestDTO options) {
        BooleanBuilder predicate = new BooleanBuilder();

        // 1) CategoryType 필터
        if (options.getCategory() != null && !options.getCategory().isEmpty()) {
            CategoryType categoryType = CategoryType.toCategoryType(options.getCategory());

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

        // 2) Region 필터
        if (options.getItemRegions() != null && !options.getItemRegions().isEmpty()) {
            BooleanBuilder regionBuilder = new BooleanBuilder();
            for (ItemRequestDTO.CollaborationRegionRequestDTO cond : options.getItemRegions()) {
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

        // 3) Position 필터링
        if (options.getPositionId() != null) {
            Long positionId = options.getPositionId();

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

        List<ItemResponseDTO.ItemResultDTO> itemResultDTOList = jpaQueryFactory
                .select(Projections.constructor(
                        ItemResponseDTO.ItemResultDTO.class,
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
                        // 프로젝트 댓글 개수
                        JPAExpressions
                                .select(itemComment.count())
                                .from(itemComment)
                                .where(itemComment.item.eq(item)),
                        // 프로젝트 좋아요 여부
                        JPAExpressions
                                .selectOne()
                                .from(itemLike)
                                .where(
                                        itemLike.item.eq(item),
                                        itemLike.member.id.eq(currentMember.getId())
                                )
                                .exists()
                ))
                .from(item)
                .join(item.member, member)
                .join(item.member.school, school)
                .where(predicate)
                .fetch();

        return ItemResponseDTO.ItemInfoListDTO.builder()
                .items(itemResultDTOList)
                .build();
    }

    @Override
    public Page<Tuple> searchItems(Pageable pageable, String category, String sort) {
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

        OrderSpecifier<?> orderSpecifier;

        if ("popular".equals(sort)) {
            orderSpecifier = item.viewCount.desc();
        } else {
            orderSpecifier = item.createdAt.desc();
        }

        // 프로젝트 댓글 수와 같이 가져오기
        List<Tuple> results = jpaQueryFactory
                .select(item, itemComment.id.count())
                .from(item)
                .leftJoin(itemComment).on(itemComment.item.eq(item))
                .where(predicate)
                .groupBy(item.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        long total = jpaQueryFactory
                .select(item.count())
                .from(item)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}

