package com.ssafy.safeRent.assessment.dto.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssessResult {
	Boolean isSafe;
	Long assessmentHouseId;
}
