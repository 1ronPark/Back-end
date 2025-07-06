package umc.lightup.domain;

import jakarta.persistence.*;

@Entity
public class Tool extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ToolType type;
}