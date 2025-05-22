package com.ssafy.safeRent.assessment.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseInfo {
	private Double latitude;
	
	private Double longitude;
	
	private Double area;
	
	private Integer price;
	
	private String address;
	
	private Integer floor;
	
	private Boolean isMember;
}
