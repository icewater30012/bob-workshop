package com.krtc.dto;

import java.math.BigDecimal;

public record RouteAnalysisDto(
        String entryStation,
        String exitStation,
        long tripCount,
        BigDecimal totalRevenue
) {
}

// Made with Bob
