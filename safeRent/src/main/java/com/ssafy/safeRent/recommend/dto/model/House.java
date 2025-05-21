package com.ssafy.safeRent.recommend.dto.model;

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
public class House {
  private Long tradedHouseId;
  private String name;
  private String price;
  private String area;
  private String builtYear;
  private String floor;
  private String address;
  private Integer order; // 인프라가 많은 순
  private Double distance; // 가장 가까운 인프라까지와의 거리
  // private String lat;
  // private String longi;
}