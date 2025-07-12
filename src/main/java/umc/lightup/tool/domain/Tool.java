package umc.lightup.tool.domain;

import jakarta.persistence.*;
import umc.lightup.tool.enums.ToolType;

@Entity
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ToolType type;
}