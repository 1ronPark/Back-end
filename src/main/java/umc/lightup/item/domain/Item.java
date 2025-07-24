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

    @Column(length = 40, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String introduce;

    @Column(length = 3000, nullable = false)
    private String description;

    @Column(name = "project_status", nullable = false)
    private boolean projectStatus;

    @Column(nullable = false)
    private String itemProfileImageUrl;

    @Column(nullable = false)
    private String itemPlanFileUrl;

    @Column(name = "github_link")
    private String githubLink;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "item_extra_links", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "extra_link")
    private List<String> extraLinks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemRegion> itemRegions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<RecruitPosition> recruitPositions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemImage> itemImages = new ArrayList<>();

    //프로젝트에 프로젝트 프로필 이미지, 기획서를 제외하고 사진을 추가로 업로드 할 수 있게 하려면 itemImages도 필요함
    public Item uploadItemProfile(String itemProfileImageUrl) {
        this.itemProfileImageUrl = itemProfileImageUrl;
        return this;
    }

    public Item uploadItemPlanFile(String itemPlanFileUrl) {
        this.itemPlanFileUrl = itemPlanFileUrl;
        return this;
    }

    public void addItemRegion(ItemRegion itemRegion) {
        this.itemRegions.add(itemRegion);
        itemRegion.assignItem(this);
    }

    public void addRecruitPosition(RecruitPosition recruitPosition) {
        this.recruitPositions.add(recruitPosition);
        recruitPosition.assignItem(this);
    }
}