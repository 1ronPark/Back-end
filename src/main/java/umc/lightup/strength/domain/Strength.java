package umc.lightup.strength.domain;

import jakarta.persistence.*;
import lombok.Getter;
import umc.lightup.common.BaseEntity;
import umc.lightup.member.domain.Member;

@Entity
@Getter
public class Strength extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = false)
    private String name;

    @Column(nullable = false)
    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    protected Strength() {}

    public static Strength createStrength(String name, boolean isCustom, Member owner) {
        Strength strength = new Strength();
        strength.name = name;
        strength.isCustom = isCustom;
        strength.owner = owner;
        return strength;
    }
}