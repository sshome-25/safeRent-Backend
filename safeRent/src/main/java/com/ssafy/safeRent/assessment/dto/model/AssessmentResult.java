package com.ssafy.safeRent.assessment.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {

	private Long id;
    private String overallAssessment;  // 종합평가
    private String riskFactor1;        // 발견된 위험요소1
    private String solution1;          // 해결방안1
    private String riskFactor2;        // 발견된 위험요소2
    private String solution2;          // 해결방안2
}
