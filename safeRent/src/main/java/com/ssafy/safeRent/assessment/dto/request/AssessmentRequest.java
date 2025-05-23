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
	private Double latitude;
	private Double longitude;
	private Integer price;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return latitude + " " + longitude + " " + price;
	}
}
