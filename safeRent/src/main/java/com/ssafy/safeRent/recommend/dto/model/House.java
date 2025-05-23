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
  private Long id;
  private String price;
  private Double latitude;
  private Double longitude;
}