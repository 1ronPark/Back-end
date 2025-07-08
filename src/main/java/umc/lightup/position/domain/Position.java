package umc.lightup.position.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import umc.lightup.common.BaseEntity;

@Entity
public class Position extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String name;
}