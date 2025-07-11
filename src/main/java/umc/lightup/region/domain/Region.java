package umc.lightup.region.domain;

import jakarta.persistence.*;
import umc.lightup.common.BaseEntity;

@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String sido;

    @Column(nullable = false, length = 30)
    private String sigungu;

}