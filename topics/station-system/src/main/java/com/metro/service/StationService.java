package com.metro.service;

import com.metro.exception.BusinessException;
import com.metro.exception.ResourceNotFoundException;
import com.metro.model.Station;
import com.metro.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 車站服務層
 * 
 * 處理車站相關的業務邏輯
 */
@Service
@Transactional
public class StationService {
    
    private static final Logger logger = LoggerFactory.getLogger(StationService.class);
    private final StationRepository stationRepository;
    
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }
    
    /**
     * 取得所有車站
     * 
     * @return 所有車站列表
     */
    public List<Station> getAllStations() {
        logger.debug("取得所有車站");
        List<Station> stations = stationRepository.findAll();
        logger.info("成功取得 {} 個車站", stations.size());
        return stations;
    }
    
    /**
     * 根據 ID 取得車站
     * 
     * @param id 車站 ID
     * @return 車站資料
     * @throws ResourceNotFoundException 如果車站不存在
     */
    @SuppressWarnings("null")
    public Station getStationById(Long id) {
        logger.debug("根據 ID 取得車站: {}", id);
        return stationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("車站不存在: ID = {}", id);
                    return new ResourceNotFoundException("車站不存在: ID = " + id);
                });
    }
    
    /**
     * 建立新車站
     * 
     * @param station 車站資料
     * @return 建立的車站
     * @throws BusinessException 如果車站代碼已存在
     */
    public Station createStation(Station station) {
        logger.debug("建立新車站: code={}, name={}", station.getCode(), station.getName());
        
        // 檢查車站代碼是否已存在
        if (stationRepository.findByCode(station.getCode()).isPresent()) {
            logger.error("車站代碼已存在: {}", station.getCode());
            throw new BusinessException("車站代碼已存在: " + station.getCode());
        }
        
        Station savedStation = stationRepository.save(station);
        logger.info("成功建立車站: id={}, code={}, name={}", 
                savedStation.getId(), savedStation.getCode(), savedStation.getName());
        return savedStation;
    }
    
    /**
     * 更新車站資料
     * 
     * @param id 車站 ID
     * @param station 更新的車站資料
     * @return 更新後的車站
     * @throws ResourceNotFoundException 如果車站不存在
     * @throws BusinessException 如果車站代碼已存在
     */
    public Station updateStation(Long id, Station station) {
        logger.debug("更新車站: id={}", id);
        Station existingStation = getStationById(id);
        
        // 如果車站代碼有變更,檢查新代碼是否已被使用
        if (!existingStation.getCode().equals(station.getCode())) {
            if (stationRepository.findByCode(station.getCode()).isPresent()) {
                logger.error("車站代碼已存在: {}", station.getCode());
                throw new BusinessException("車站代碼已存在: " + station.getCode());
            }
        }
        
        existingStation.setCode(station.getCode());
        existingStation.setName(station.getName());
        existingStation.setLine(station.getLine());
        
        Station updatedStation = stationRepository.save(existingStation);
        logger.info("成功更新車站: id={}, code={}, name={}", 
                updatedStation.getId(), updatedStation.getCode(), updatedStation.getName());
        return updatedStation;
    }
    
    /**
     * 刪除車站
     * 
     * @param id 車站 ID
     * @throws ResourceNotFoundException 如果車站不存在
     */
    @SuppressWarnings("null")
    public void deleteStation(Long id) {
        logger.debug("刪除車站: id={}", id);
        if (!stationRepository.existsById(id)) {
            logger.error("車站不存在: ID = {}", id);
            throw new ResourceNotFoundException("車站不存在: ID = " + id);
        }
        stationRepository.deleteById(id);
        logger.info("成功刪除車站: id={}", id);
    }
    
    /**
     * 根據路線取得車站
     * 
     * @param line 路線名稱
     * @return 該路線的所有車站
     */
    public List<Station> getStationsByLine(String line) {
        logger.debug("根據路線取得車站: {}", line);
        List<Station> stations = stationRepository.findByLine(line);
        logger.info("成功取得路線 {} 的 {} 個車站", line, stations.size());
        return stations;
    }
}

// Made with Bob
