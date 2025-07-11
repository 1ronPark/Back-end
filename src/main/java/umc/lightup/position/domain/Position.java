package umc.lightup.position.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;

@Entity
public class Position extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String name;
}