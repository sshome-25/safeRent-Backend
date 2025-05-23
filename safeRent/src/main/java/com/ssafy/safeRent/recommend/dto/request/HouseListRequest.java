package com.ssafy.safeRent.recommend.dto.request;

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
public class HouseListRequest {
  private Double swLat;
  private Double swLng;
  private Double neLat;
  private Double neLng;
  private Integer limit;

  @Override
  public String toString() {
    return swLat + " " + swLng + " " + neLat + " " + neLng + " " + limit;
  }
}