package com.ssafy.safeRent.assessment.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistic {
    private Integer tradeCnt;

    private Double avgPrice;

    private Double minPrice;

    private Double maxPrice;

    private Double pricePerArea;

    @Override
    public String toString() {
        return "Statistic{" +
            "tradeCnt=" + tradeCnt +
            ", avgPrice=" + avgPrice +
            ", minPrice=" + minPrice +
            ", maxPrice=" + maxPrice +
            ", pricePerArea=" + pricePerArea +
            '}';
    }
}
