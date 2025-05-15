package com.ssafy.safeRent.recommend.dto.response;

import java.util.List;

import com.ssafy.safeRent.recommend.dto.model.House;

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
public class HouseListResponse {
  private List<House> houseList;
}
