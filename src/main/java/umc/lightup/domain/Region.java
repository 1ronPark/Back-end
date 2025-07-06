package umc.lightup.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Region extends BaseEntity {
    @Column(nullable = false, length = 20)
    private String sido;

    @Column(nullable = false, length = 30)
    private String sigungu;

    @Column(unique = true, length = 50)
    private String fullName;
}