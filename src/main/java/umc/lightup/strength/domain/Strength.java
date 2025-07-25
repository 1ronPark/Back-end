package umc.lightup.strength.domain;

import jakarta.persistence.*;
import lombok.*;

import umc.lightup.strength.enums.StrengthType;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Strength {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private StrengthType strengthType;

    //커스텀 강점 생성 기능 삭제
/*    @Column(nullable = false)
    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    public static Strength createStrength(String name, boolean isCustom, Member owner) {
        Strength strength = new Strength();
        strength.name = name;
        strength.isCustom = isCustom;
        strength.owner = owner;
        return strength;
    }*/
}