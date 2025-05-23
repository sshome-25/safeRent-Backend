package com.ssafy.safeRent.recommend.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ssafy.safeRent.recommend.dto.model.House;
import com.ssafy.safeRent.recommend.dto.request.FavoriteHouseRequest;
import com.ssafy.safeRent.recommend.dto.request.HouseListRequest;
import com.ssafy.safeRent.recommend.dto.request.RecommendedHouseRequest;
import com.ssafy.safeRent.recommend.dto.response.HouseDetailResponse;
import com.ssafy.safeRent.recommend.dto.response.HouseListResponse;
import com.ssafy.safeRent.recommend.repository.RecommendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendService {

  private final RecommendRepository recommendRepository;

  // 1. 매물 목록 조회
  public HouseListResponse getHouseList(HouseListRequest request) {
    List<House> houseList = recommendRepository.selectTradedHouses(request);
    System.out.println(houseList.get(0).getAptDong());
    return HouseListResponse.builder()
        .houseList(houseList)
        .build();
  }

  // 2. 매물 상세 조회
  public HouseDetailResponse getHouseDetail(Long houseId) {
    HouseDetailResponse house = recommendRepository.selectTradedHousesDetail(houseId);
    if (house == null) {
      throw new IllegalArgumentException("House not found with ID: " + houseId);
    }
    return house;
  }

  // 3. 추천 매물 조회
//  public HouseListResponse getRecommendedHouses(RecommendedHouseRequest request) {
//    List<House> houseList = recommendRepository.selectTradedHouses(request.getLat(), request.getLongi());
//
//    // 1. infraCount 기준 정렬
//    boolean allInfraCountZero = true;
//
//    for (House house : houseList) {
//      Integer infraCount = recommendRepository.countInfraWithinRadius(request);
//      house.setOrder(infraCount != null ? infraCount : 0);
//      if (infraCount != null && infraCount > 0) {
//        allInfraCountZero = false;
//      }
//    }
//    // infraCount 기준 오름차순 정렬
//    houseList.sort((h1, h2) -> Integer.compare(h1.getOrder(), h2.getOrder()));
//
//    // 2. 모든 infraCount가 0이면 nearestInfra 기준 정렬
//    if (allInfraCountZero) {
//      for (House house : houseList) {
//        Map<String, Object> nearestInfra = recommendRepository.findNearestInfra(request);
//        Double distance = nearestInfra != null && nearestInfra.get("distance") != null
//            ? Double.parseDouble(nearestInfra.get("distance").toString())
//            : Double.MAX_VALUE;
//        house.setDistance(distance);
//        house.setOrder(distance.intValue()); // Double → Integer로 변환
//      }
//      // nearestInfra 기준 오름차순 정렬
//      houseList.sort((h1, h2) -> Integer.compare(h1.getOrder(), h2.getOrder()));
//    }
//
//    return HouseListResponse.builder()
//        .houseList(houseList)
//        .build();
//  }

  // 4. 매물 관심목록 추가
  public void addFavorite(FavoriteHouseRequest request) {
    recommendRepository.insertFavorites(request.getUserId(), request.getHouseId());
  }
}
