package com.ssafy.safeRent.assessment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AssessmentResponse {
	
	private String address;
	// 위도
	private Double latitude;
	// 경도
	private Double longitude;
	
	private Boolean isSafe;
	
	// 평가 내용
	private String content;
}
