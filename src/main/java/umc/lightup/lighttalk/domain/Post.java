package umc.lightup.lighttalk.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Setter
    @Column(nullable = false)
    private String content;

    @Builder.Default
    private Long likes = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member postMember;

    //학교 추가
/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;*/

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private List<Comment> postComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private List<PostImage> postImages = new ArrayList<>();
}
