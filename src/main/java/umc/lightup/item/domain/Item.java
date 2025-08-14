package umc.lightup.item.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    @Column(length = 40, nullable = false)
    private String name;

    @Setter
    @Column(length = 50, nullable = false)
    private String introduce;

    @Setter
    @Lob
    @Column(nullable = false)
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCategory> itemCategories = new ArrayList<>();

    @Setter
    @Column(name = "project_status", nullable = false)
    private boolean projectStatus;

    @Setter
    @Column(nullable = false)
    private String itemProfileImageUrl;

    @Setter
    @Column(nullable = false)
    private String itemPlanFileUrl;

    @Setter
    @Column(name = "extra_link1")
    private String extraLink1;

    @Setter
    @Column(name = "extra_link2")
    private String extraLink2;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRegion> itemRegions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitPosition> recruitPositions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemImage> itemImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemComment> itemComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemApply> itemApplyList = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Long viewCount = 0L;

    //프로젝트에 프로젝트 프로필 이미지, 기획서를 제외하고 사진을 추가로 업로드 할 수 있게 하려면 itemImages도 필요함
    public Item uploadItemProfile(String itemProfileImageUrl) {
        this.itemProfileImageUrl = itemProfileImageUrl;
        return this;
    }

    public Item uploadItemPlanFile(String itemPlanFileUrl) {
        this.itemPlanFileUrl = itemPlanFileUrl;
        return this;
    }

    public void addItemCategory(ItemCategory itemCategory) {
        this.itemCategories.add(itemCategory);
        itemCategory.assignItem(this);
    }

    public void addItemRegion(ItemRegion itemRegion) {
        this.itemRegions.add(itemRegion);
        itemRegion.assignItem(this);
    }

    public void addRecruitPosition(RecruitPosition recruitPosition) {
        this.recruitPositions.add(recruitPosition);
        recruitPosition.assignItem(this);
    }

    public void clearItemCategories() {
        this.itemCategories.clear();
    }

    public void clearItemRegions() {
        this.itemRegions.clear();
    }

    public void clearRecruitPositions() {
        this.recruitPositions.clear();
    }
}