package com.ssafy.safeRent.assessment.dto.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterResult {
	private Long registerId;
	
	private AssessmentResult grokResponse;
}
