package com.ssafy.safeRent.assessment.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class AssessmentHouse {
	
	private Long id;
	
	private Integer price;
	
	private Double area;
	
	private String address;
	// 위도
	private Double latitude;
	// 경도
	private Double longitude;
	
	private Boolean isSafe;
	
	private Integer marketPrice;
	
	private Integer floor;
}
