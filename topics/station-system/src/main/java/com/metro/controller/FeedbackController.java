package com.metro.controller;

import com.metro.model.Feedback;
import com.metro.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 回饋 REST API 控制器
 *
 * 提供回饋管理的 RESTful API
 */
@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class FeedbackController {
    
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    /**
     * 取得所有回饋
     * 
     * GET /api/feedbacks
     * 
     * @return 所有回饋列表
     */
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        logger.info("API 請求: GET /api/feedbacks - 取得所有回饋");
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }
    
    /**
     * 根據 ID 取得回饋
     * 
     * GET /api/feedbacks/{id}
     * 
     * @param id 回饋 ID
     * @return 回饋資料
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        logger.info("API 請求: GET /api/feedbacks/{} - 取得回饋", id);
        Feedback feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }
    
    /**
     * 建立新回饋
     * 
     * POST /api/feedbacks
     * 
     * Request Body:
     * {
     *   "station": { "id": 1 },
     *   "rating": 5,
     *   "comment": "服務很好",
     *   "passengerName": "王小明"
     * }
     * 
     * @param feedback 回饋資料
     * @return 建立的回饋
     */
    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody Feedback feedback) {
        logger.info("API 請求: POST /api/feedbacks - 建立新回饋");
        Feedback createdFeedback = feedbackService.createFeedback(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
    }
    
    /**
     * 更新回饋資料
     * 
     * PUT /api/feedbacks/{id}
     * 
     * @param id 回饋 ID
     * @param feedback 更新的回饋資料
     * @return 更新後的回饋
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        logger.info("API 請求: PUT /api/feedbacks/{} - 更新回饋", id);
        Feedback updatedFeedback = feedbackService.updateFeedback(id, feedback);
        return ResponseEntity.ok(updatedFeedback);
    }
    
    /**
     * 刪除回饋
     * 
     * DELETE /api/feedbacks/{id}
     * 
     * @param id 回饋 ID
     * @return 無內容回應
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        logger.info("API 請求: DELETE /api/feedbacks/{} - 刪除回饋", id);
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根據車站 ID 取得回饋
     * 
     * GET /api/feedbacks/station/{stationId}
     * 
     * @param stationId 車站 ID
     * @return 該車站的所有回饋
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<?> getFeedbacksByStationId(@PathVariable Long stationId) {
        logger.info("API 請求: GET /api/feedbacks/station/{} - 取得車站回饋", stationId);
        List<Feedback> feedbacks = feedbackService.getFeedbacksByStationId(stationId);
        return ResponseEntity.ok(feedbacks);
    }
    
    /**
     * 根據評分取得回饋
     * 
     * GET /api/feedbacks/rating/{rating}
     * 
     * @param rating 評分 (1-5)
     * @return 符合評分的所有回饋
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<?> getFeedbacksByRating(@PathVariable Integer rating) {
        logger.info("API 請求: GET /api/feedbacks/rating/{} - 取得評分回饋", rating);
        List<Feedback> feedbacks = feedbackService.getFeedbacksByRating(rating);
        return ResponseEntity.ok(feedbacks);
    }
    
    /**
     * 取得整體回饋統計資料
     * 
     * GET /api/feedbacks/statistics
     * 
     * Response:
     * {
     *   "totalFeedbacks": 100,
     *   "averageRating": 4.2,
     *   "ratingDistribution": {
     *     "1": 5,
     *     "2": 10,
     *     "3": 15,
     *     "4": 30,
     *     "5": 40
     *   }
     * }
     * 
     * @return 統計資料
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFeedbackStatistics() {
        logger.info("API 請求: GET /api/feedbacks/statistics - 取得回饋統計");
        Map<String, Object> statistics = feedbackService.getFeedbackStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 取得特定車站的統計資料
     * 
     * GET /api/feedbacks/statistics/station/{stationId}
     * 
     * Response:
     * {
     *   "stationId": 1,
     *   "stationName": "台北車站",
     *   "stationCode": "R10",
     *   "stationLine": "紅線",
     *   "totalFeedbacks": 50,
     *   "averageRating": 4.5,
     *   "ratingDistribution": {
     *     "1": 2,
     *     "2": 3,
     *     "3": 5,
     *     "4": 15,
     *     "5": 25
     *   }
     * }
     * 
     * @param stationId 車站 ID
     * @return 該車站的統計資料
     */
    @GetMapping("/statistics/station/{stationId}")
    public ResponseEntity<?> getStationStatistics(@PathVariable Long stationId) {
        logger.info("API 請求: GET /api/feedbacks/statistics/station/{} - 取得車站統計", stationId);
        Map<String, Object> statistics = feedbackService.getStationStatistics(stationId);
        return ResponseEntity.ok(statistics);
    }
}

// Made with Bob