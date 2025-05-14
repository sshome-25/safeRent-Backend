package com.ssafy.safeRent.assessment.dto.Response;

import lombok.Getter;

@Getter
public class AssessmentResponse {

	// 매물 진단 점수 (주변 시세가를 토대로 전세가가 몇퍼센트의 비중인지)
	private Integer score;
	
}
