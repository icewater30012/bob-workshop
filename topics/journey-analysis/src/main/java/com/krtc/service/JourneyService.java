package com.krtc.service;

import com.krtc.dto.JourneyStatisticsDto;
import com.krtc.dto.PeakHourDto;
import com.krtc.dto.RouteAnalysisDto;
import com.krtc.model.Journey;
import com.krtc.repository.JourneyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JourneyService {

    private final JourneyRepository journeyRepository;

    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    public List<Journey> getAllJourneys() {
        return journeyRepository.findAll();
    }

    public JourneyStatisticsDto getStatistics() {
        List<Journey> journeys = journeyRepository.findAll();
        long totalJourneys = journeys.size();
        BigDecimal totalRevenue = journeys.stream()
                .map(Journey::getFare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        java.util.Map<String, Long> routeCounts = journeys.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        j -> j.getEntryStation().getStationName() + " → " + j.getExitStation().getStationName(),
                        java.util.stream.Collectors.counting()));

        String busiestRoute = routeCounts.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("-");

        java.util.Map<Integer, Long> hourCounts = journeys.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        j -> j.getEntryTime().getHour(),
                        java.util.stream.Collectors.counting()));

        String peakHourRange = hourCounts.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(e -> String.format("%02d:00-%02d:59", e.getKey(), e.getKey()))
                .orElse("-");

        return new JourneyStatisticsDto(totalJourneys, totalRevenue, busiestRoute, peakHourRange);
    }

    public List<RouteAnalysisDto> getPopularRoutes() {
        return journeyRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        j -> j.getEntryStation().getStationName() + "|" + j.getExitStation().getStationName(),
                        java.util.stream.Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Journey> journeys = entry.getValue();
                    Journey first = journeys.get(0);
                    BigDecimal revenue = journeys.stream()
                            .map(Journey::getFare)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new RouteAnalysisDto(
                            first.getEntryStation().getStationName(),
                            first.getExitStation().getStationName(),
                            journeys.size(),
                            revenue
                    );
                })
                .sorted((a, b) -> {
                    int countCompare = Long.compare(b.tripCount(), a.tripCount());
                    if (countCompare != 0) {
                        return countCompare;
                    }
                    return b.totalRevenue().compareTo(a.totalRevenue());
                })
                .limit(10)
                .toList();
    }

    public List<PeakHourDto> getPeakHours() {
        java.util.Map<Integer, Long> counts = journeyRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        j -> j.getEntryTime().getHour(),
                        java.util.stream.Collectors.counting()));

        return java.util.stream.IntStream.range(0, 24)
                .mapToObj(hour -> new PeakHourDto(hour, counts.getOrDefault(hour, 0L)))
                .toList();
    }
}

// Made with Bob
