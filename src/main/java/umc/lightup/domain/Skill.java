package umc.lightup.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Skill extends BaseEntity {
    @Column(length = 30, nullable = false, unique = true)
    private String name;
}