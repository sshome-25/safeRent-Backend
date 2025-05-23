package com.ssafy.safeRent.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class FavoriteHouseRequest {
  @JsonProperty("house_id")
  private Long houseId;

  @JsonProperty("user_id")
  private Long userId;
}