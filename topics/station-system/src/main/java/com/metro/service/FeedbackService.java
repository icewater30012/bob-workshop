package com.metro.service;

import com.metro.exception.BusinessException;
import com.metro.exception.ResourceNotFoundException;
import com.metro.model.Feedback;
import com.metro.model.Station;
import com.metro.repository.FeedbackRepository;
import com.metro.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回饋服務層
 * 
 * 處理回饋相關的業務邏輯
 */
@Service
@Transactional
public class FeedbackService {
    
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    private final FeedbackRepository feedbackRepository;
    private final StationRepository stationRepository;
    
    public FeedbackService(FeedbackRepository feedbackRepository, StationRepository stationRepository) {
        this.feedbackRepository = feedbackRepository;
        this.stationRepository = stationRepository;
    }
    
    /**
     * 取得所有回饋
     * 
     * @return 所有回饋列表
     */
    public List<Feedback> getAllFeedbacks() {
        logger.debug("取得所有回饋");
        List<Feedback> feedbacks = feedbackRepository.findAll();
        logger.info("成功取得 {} 筆回饋", feedbacks.size());
        return feedbacks;
    }
    
    /**
     * 根據 ID 取得回饋
     * 
     * @param id 回饋 ID
     * @return 回饋資料
     * @throws ResourceNotFoundException 如果回饋不存在
     */
    @SuppressWarnings("null")
    public Feedback getFeedbackById(Long id) {
        logger.debug("根據 ID 取得回饋: {}", id);
        return feedbackRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("回饋不存在: ID = {}", id);
                    return new ResourceNotFoundException("回饋不存在: ID = " + id);
                });
    }
    
    /**
     * 建立新回饋
     * 
     * @param feedback 回饋資料
     * @return 建立的回饋
     * @throws BusinessException 如果車站不存在或資料驗證失敗
     */
    public Feedback createFeedback(Feedback feedback) {
        logger.debug("建立新回饋: stationId={}, rating={}", 
                feedback.getStation() != null ? feedback.getStation().getId() : null, 
                feedback.getRating());
        
        // 驗證車站是否存在
        if (feedback.getStation() == null || feedback.getStation().getId() == null) {
            logger.error("必須指定車站");
            throw new BusinessException("必須指定車站");
        }
        
        @SuppressWarnings("null")
        Station station = stationRepository.findById(feedback.getStation().getId())
                .orElseThrow(() -> {
                    logger.error("車站不存在: ID = {}", feedback.getStation().getId());
                    return new ResourceNotFoundException("車站不存在: ID = " + feedback.getStation().getId());
                });
        
        feedback.setStation(station);
        
        // 驗證評分範圍
        if (feedback.getRating() == null || feedback.getRating() < 1 || feedback.getRating() > 5) {
            logger.error("評分必須在 1-5 之間: {}", feedback.getRating());
            throw new BusinessException("評分必須在 1-5 之間");
        }
        
        // 驗證回饋內容
        if (feedback.getComment() == null || feedback.getComment().trim().isEmpty()) {
            logger.error("回饋內容不能為空");
            throw new BusinessException("回饋內容不能為空");
        }
        
        if (feedback.getComment().length() > 1000) {
            logger.error("回饋內容不能超過 1000 字: {} 字", feedback.getComment().length());
            throw new BusinessException("回饋內容不能超過 1000 字");
        }
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        logger.info("成功建立回饋: id={}, stationId={}, rating={}", 
                savedFeedback.getId(), savedFeedback.getStation().getId(), savedFeedback.getRating());
        return savedFeedback;
    }
    
    /**
     * 更新回饋資料
     * 
     * @param id 回饋 ID
     * @param feedback 更新的回饋資料
     * @return 更新後的回饋
     * @throws ResourceNotFoundException 如果回饋或車站不存在
     * @throws BusinessException 如果資料驗證失敗
     */
    public Feedback updateFeedback(Long id, Feedback feedback) {
        logger.debug("更新回饋: id={}", id);
        Feedback existingFeedback = getFeedbackById(id);
        
        // 如果要更新車站,驗證車站是否存在
        if (feedback.getStation() != null && feedback.getStation().getId() != null) {
            @SuppressWarnings("null")
            Station station = stationRepository.findById(feedback.getStation().getId())
                    .orElseThrow(() -> {
                        logger.error("車站不存在: ID = {}", feedback.getStation().getId());
                        return new ResourceNotFoundException("車站不存在: ID = " + feedback.getStation().getId());
                    });
            existingFeedback.setStation(station);
        }
        
        // 更新評分
        if (feedback.getRating() != null) {
            if (feedback.getRating() < 1 || feedback.getRating() > 5) {
                logger.error("評分必須在 1-5 之間: {}", feedback.getRating());
                throw new BusinessException("評分必須在 1-5 之間");
            }
            existingFeedback.setRating(feedback.getRating());
        }
        
        // 更新回饋內容
        if (feedback.getComment() != null) {
            if (feedback.getComment().trim().isEmpty()) {
                logger.error("回饋內容不能為空");
                throw new BusinessException("回饋內容不能為空");
            }
            if (feedback.getComment().length() > 1000) {
                logger.error("回饋內容不能超過 1000 字: {} 字", feedback.getComment().length());
                throw new BusinessException("回饋內容不能超過 1000 字");
            }
            existingFeedback.setComment(feedback.getComment());
        }
        
        // 更新乘客姓名
        if (feedback.getPassengerName() != null) {
            existingFeedback.setPassengerName(feedback.getPassengerName());
        }
        
        Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
        logger.info("成功更新回饋: id={}, rating={}", updatedFeedback.getId(), updatedFeedback.getRating());
        return updatedFeedback;
    }
    
    /**
     * 刪除回饋
     * 
     * @param id 回饋 ID
     * @throws ResourceNotFoundException 如果回饋不存在
     */
    @SuppressWarnings("null")
    public void deleteFeedback(Long id) {
        logger.debug("刪除回饋: id={}", id);
        if (!feedbackRepository.existsById(id)) {
            logger.error("回饋不存在: ID = {}", id);
            throw new ResourceNotFoundException("回饋不存在: ID = " + id);
        }
        feedbackRepository.deleteById(id);
        logger.info("成功刪除回饋: id={}", id);
    }
    
    /**
     * 根據車站 ID 取得回饋
     * 
     * @param stationId 車站 ID
     * @return 該車站的所有回饋
     * @throws ResourceNotFoundException 如果車站不存在
     */
    public List<Feedback> getFeedbacksByStationId(Long stationId) {
        logger.debug("根據車站 ID 取得回饋: {}", stationId);
        
        // 驗證車站是否存在
        @SuppressWarnings("null")
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> {
                    logger.error("車站不存在: ID = {}", stationId);
                    return new ResourceNotFoundException("車站不存在: ID = " + stationId);
                });
        
        List<Feedback> feedbacks = feedbackRepository.findByStation(station);
        logger.info("成功取得車站 {} 的 {} 筆回饋", stationId, feedbacks.size());
        return feedbacks;
    }
    
    /**
     * 根據評分取得回饋
     * 
     * @param rating 評分 (1-5)
     * @return 符合評分的所有回饋
     * @throws BusinessException 如果評分不在有效範圍內
     */
    public List<Feedback> getFeedbacksByRating(Integer rating) {
        logger.debug("根據評分取得回饋: {}", rating);
        if (rating < 1 || rating > 5) {
            logger.error("評分必須在 1-5 之間: {}", rating);
            throw new BusinessException("評分必須在 1-5 之間");
        }
        List<Feedback> feedbacks = feedbackRepository.findByRating(rating);
        logger.info("成功取得評分 {} 的 {} 筆回饋", rating, feedbacks.size());
        return feedbacks;
    }
    
    /**
     * 取得回饋統計資料
     * 
     * @return 統計資料 Map
     */
    public Map<String, Object> getFeedbackStatistics() {
        logger.debug("取得回饋統計資料");
        Map<String, Object> statistics = new HashMap<>();
        
        // 總回饋數
        long totalFeedbacks = feedbackRepository.count();
        statistics.put("totalFeedbacks", totalFeedbacks);
        
        // 整體平均評分
        Double averageRating = feedbackRepository.getOverallAverageRating();
        statistics.put("averageRating", averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0);
        
        // 各評分的回饋數量
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = feedbackRepository.findByRating(i).size();
            ratingDistribution.put(i, count);
        }
        statistics.put("ratingDistribution", ratingDistribution);
        
        logger.info("成功取得回饋統計資料: 總數={}, 平均評分={}", totalFeedbacks, averageRating);
        return statistics;
    }
    
    /**
     * 取得特定車站的統計資料
     * 
     * @param stationId 車站 ID
     * @return 該車站的統計資料
     * @throws ResourceNotFoundException 如果車站不存在
     */
    public Map<String, Object> getStationStatistics(Long stationId) {
        logger.debug("取得車站統計資料: stationId={}", stationId);
        
        // 驗證車站是否存在
        @SuppressWarnings("null")
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> {
                    logger.error("車站不存在: ID = {}", stationId);
                    return new ResourceNotFoundException("車站不存在: ID = " + stationId);
                });
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 車站資訊
        statistics.put("stationId", station.getId());
        statistics.put("stationName", station.getName());
        statistics.put("stationCode", station.getCode());
        statistics.put("stationLine", station.getLine());
        
        // 回饋總數
        Long totalFeedbacks = feedbackRepository.countByStationId(stationId);
        statistics.put("totalFeedbacks", totalFeedbacks);
        
        // 平均評分
        Double averageRating = feedbackRepository.getAverageRatingByStationId(stationId);
        statistics.put("averageRating", averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0);
        
        // 各評分的回饋數量
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = feedbackRepository.findByStationIdAndRating(stationId, i).size();
            ratingDistribution.put(i, count);
        }
        statistics.put("ratingDistribution", ratingDistribution);
        
        logger.info("成功取得車站 {} 的統計資料: 總數={}, 平均評分={}", 
                stationId, totalFeedbacks, averageRating);
        return statistics;
    }
}

// Made with Bob