
package com.localmind.memory;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "memory")
@Getter
@Data
@Setter
public class MemoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // FACT, PROJECT, PREFERENCE

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public MemoryEntity() {}

    public MemoryEntity(String type, String content) {
        this.type = type;
        this.content = content;
        this.createdAt = Instant.now();
    }
}


