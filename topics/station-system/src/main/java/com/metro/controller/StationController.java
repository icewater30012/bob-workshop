package com.metro.controller;

import com.metro.model.Station;
import com.metro.service.StationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 車站 REST API 控制器
 *
 * 提供車站管理的 RESTful API
 */
@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "*")
public class StationController {
    
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);
    private final StationService stationService;
    
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }
    
    /**
     * 取得所有車站
     * 
     * @return 所有車站列表
     */
    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        logger.info("API 請求: GET /api/stations - 取得所有車站");
        List<Station> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }
    
    /**
     * 根據 ID 取得車站
     * 
     * @param id 車站 ID
     * @return 車站資料
     */
    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable Long id) {
        logger.info("API 請求: GET /api/stations/{} - 取得車站", id);
        Station station = stationService.getStationById(id);
        return ResponseEntity.ok(station);
    }
    
    /**
     * 建立新車站
     * 
     * @param station 車站資料
     * @return 建立的車站
     */
    @PostMapping
    public ResponseEntity<?> createStation(@Valid @RequestBody Station station, BindingResult bindingResult) {
        logger.info("API 請求: POST /api/stations - 建立新車站: code={}", station.getCode());
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            logger.warn("資料驗證失敗: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        
        Station createdStation = stationService.createStation(station);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStation);
    }
    
    /**
     * 更新車站資料
     * 
     * @param id 車站 ID
     * @param station 更新的車站資料
     * @return 更新後的車站
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStation(@PathVariable Long id, @Valid @RequestBody Station station, BindingResult bindingResult) {
        logger.info("API 請求: PUT /api/stations/{} - 更新車站", id);
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            logger.warn("資料驗證失敗: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        
        Station updatedStation = stationService.updateStation(id, station);
        return ResponseEntity.ok(updatedStation);
    }
    
    /**
     * 刪除車站
     * 
     * @param id 車站 ID
     * @return 無內容回應
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        logger.info("API 請求: DELETE /api/stations/{} - 刪除車站", id);
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根據路線取得車站
     * 
     * @param line 路線名稱
     * @return 該路線的所有車站
     */
    @GetMapping("/line/{line}")
    public ResponseEntity<List<Station>> getStationsByLine(@PathVariable String line) {
        logger.info("API 請求: GET /api/stations/line/{} - 取得路線車站", line);
        List<Station> stations = stationService.getStationsByLine(line);
        return ResponseEntity.ok(stations);
    }
}

// Made with Bob
