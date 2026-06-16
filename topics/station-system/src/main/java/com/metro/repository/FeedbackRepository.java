package com.metro.repository;

import com.metro.model.Feedback;
import com.metro.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 回饋資料存取層
 * 
 * 提供回饋資料的 CRUD 操作與查詢功能
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    /**
     * 根據車站查詢所有回饋
     * 
     * @param station 車站實體
     * @return 該車站的所有回饋
     */
    List<Feedback> findByStation(Station station);
    
    /**
     * 根據車站 ID 查詢所有回饋
     * 
     * @param stationId 車站 ID
     * @return 該車站的所有回饋
     */
    List<Feedback> findByStationId(Long stationId);
    
    /**
     * 根據評分查詢回饋
     * 
     * @param rating 評分 (1-5)
     * @return 符合評分的所有回饋
     */
    List<Feedback> findByRating(Integer rating);
    
    /**
     * 查詢評分大於等於指定值的回饋
     * 
     * @param rating 最低評分
     * @return 符合條件的回饋
     */
    List<Feedback> findByRatingGreaterThanEqual(Integer rating);
    
    /**
     * 查詢評分小於等於指定值的回饋
     * 
     * @param rating 最高評分
     * @return 符合條件的回饋
     */
    List<Feedback> findByRatingLessThanEqual(Integer rating);
    
    /**
     * 計算特定車站的回饋總數
     * 
     * @param stationId 車站 ID
     * @return 回饋總數
     */
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.station.id = :stationId")
    Long countByStationId(@Param("stationId") Long stationId);
    
    /**
     * 計算特定車站的平均評分
     * 
     * @param stationId 車站 ID
     * @return 平均評分
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.station.id = :stationId")
    Double getAverageRatingByStationId(@Param("stationId") Long stationId);
    
    /**
     * 取得所有車站的平均評分
     * 
     * @return 整體平均評分
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getOverallAverageRating();
    
    /**
     * 根據車站 ID 和評分查詢回饋
     * 
     * @param stationId 車站 ID
     * @param rating 評分
     * @return 符合條件的回饋
     */
    @Query("SELECT f FROM Feedback f WHERE f.station.id = :stationId AND f.rating = :rating")
    List<Feedback> findByStationIdAndRating(@Param("stationId") Long stationId, @Param("rating") Integer rating);
}

// Made with Bob