package com.localmind.memory;

import jakarta.persistence.*;

@Entity
@Table(name = "memory")
public class MemoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private Long createdAtEpoch;

    public MemoryEntity() {
    }

    public MemoryEntity(String type, String content) {
        this.type = type;
        this.content = content;
        this.createdAtEpoch = System.currentTimeMillis();
    }


    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatedAtEpoch() {
        return createdAtEpoch;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAtEpoch(Long createdAtEpoch) {
        this.createdAtEpoch = createdAtEpoch;
    }
}
