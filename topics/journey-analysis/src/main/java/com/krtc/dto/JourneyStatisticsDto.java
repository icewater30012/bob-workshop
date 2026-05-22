package com.krtc.dto;

import java.math.BigDecimal;

public record JourneyStatisticsDto(
    long totalJourneys,
    BigDecimal totalRevenue,
    String busiestRoute,
    String peakHourRange
) {}

// Made with Bob