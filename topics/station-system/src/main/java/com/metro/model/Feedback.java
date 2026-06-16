package com.metro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 回饋實體
 *
 * 代表乘客對車站的回饋記錄
 */
@Entity
@Table(name = "feedback")
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    
    @Column(nullable = false)
    @Min(value = 1, message = "評分必須在 1-5 之間")
    @Max(value = 5, message = "評分必須在 1-5 之間")
    private Integer rating;
    
    @Column(nullable = false, length = 1000)
    @NotBlank(message = "回饋內容不能為空")
    @Size(max = 1000, message = "回饋內容不能超過 1000 字")
    private String comment;
    
    @Column(length = 100)
    @Size(max = 100, message = "乘客姓名不能超過 100 字")
    private String passengerName;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Feedback() {
    }
    
    public Feedback(Station station, Integer rating, String comment) {
        this.station = station;
        this.rating = rating;
        this.comment = comment;
    }
    
    public Feedback(Station station, Integer rating, String comment, String passengerName) {
        this.station = station;
        this.rating = rating;
        this.comment = comment;
        this.passengerName = passengerName;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Station getStation() {
        return station;
    }
    
    public void setStation(Station station) {
        this.station = station;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getPassengerName() {
        return passengerName;
    }
    
    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", stationId=" + (station != null ? station.getId() : null) +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

// Made with Bob