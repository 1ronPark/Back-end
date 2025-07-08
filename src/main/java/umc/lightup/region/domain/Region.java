package umc.lightup.region.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import umc.lightup.common.BaseEntity;

@Entity
public class Region extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 20)
    private String sido;

    @Column(nullable = false, length = 30)
    private String sigungu;

    @Column(unique = true, length = 50)
    private String fullName;
}