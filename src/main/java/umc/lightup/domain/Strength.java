package umc.lightup.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Strength extends BaseEntity {
    @Column(length = 30, nullable = false)
    private String name;
}