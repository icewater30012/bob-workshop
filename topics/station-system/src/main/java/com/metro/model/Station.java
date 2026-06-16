package com.metro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 車站實體
 *
 * 代表捷運的一個車站
 */
@Entity
@Table(name = "station")
public class Station {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "車站代碼不能為空")
    @Size(min = 1, max = 10, message = "車站代碼長度必須在 1-10 之間")
    @Column(nullable = false, unique = true, length = 10)
    private String code;
    
    @NotBlank(message = "車站名稱不能為空")
    @Size(min = 1, max = 100, message = "車站名稱長度必須在 1-100 之間")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "路線不能為空")
    @Size(min = 1, max = 20, message = "路線長度必須在 1-20 之間")
    @Column(nullable = false, length = 20)
    private String line;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Station() {
    }
    
    public Station(String code, String name, String line) {
        this.code = code;
        this.name = name;
        this.line = line;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLine() {
        return line;
    }
    
    public void setLine(String line) {
        this.line = line;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", line='" + line + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

// Made with Bob
