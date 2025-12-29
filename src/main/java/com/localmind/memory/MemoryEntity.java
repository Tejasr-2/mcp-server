package com.localmind.memory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memory")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // FACT, PROJECT, PREFERENCE

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private Long createdAtEpoch;

    public MemoryEntity(String type, String content) {
        this.type = type;
        this.content = content;
        this.createdAtEpoch = System.currentTimeMillis();
    }
}
