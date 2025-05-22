package com.ssafy.safeRent.assessment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HouseInfoRequest {
	private Double latitude;
	
	private Double longitude;
	
	private Double area;
	
	private Integer price;
	
	private String address;
	
	private Integer floor;
}
