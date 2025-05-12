package com.ssafy.safeRent.assessment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AssessmentRequest {
	private String location;
	private Integer price;
}
