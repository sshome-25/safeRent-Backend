package com.ssafy.safeRent.recommend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseDetailResponse {
  private String name;
  private Integer price;
  private float area;
  private Integer builtYear;
  private Integer floor;
  private String address;
}