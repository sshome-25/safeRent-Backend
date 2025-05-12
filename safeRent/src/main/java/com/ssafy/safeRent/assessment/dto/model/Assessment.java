package com.ssafy.safeRent.assessment.dto.model;

import com.ssafy.safeRent.assessment.dto.enums.AssessmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {
	
	private Integer assessmentId;
	
	private String location;
	// 위도
	private Integer latitude;
	// 경도
	private Integer longitude;
	
	private Integer score;
	
	private AssessmentStatus status;
	
	private Long userId;
	
	private Integer registerId;
	
	private Integer contractId;
	
	private Integer registerAnalysisId;

	private Integer contractAnalysisId;
	
	private Integer assessmentHouseId;
}
