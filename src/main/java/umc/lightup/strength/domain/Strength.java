package umc.lightup.strength.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;

@Entity
public class Strength extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String name;
}