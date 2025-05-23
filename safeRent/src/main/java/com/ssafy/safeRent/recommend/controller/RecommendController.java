package com.ssafy.safeRent.recommend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.safeRent.recommend.dto.enums.Infras;
import com.ssafy.safeRent.recommend.dto.request.FavoriteHouseRequest;
import com.ssafy.safeRent.recommend.dto.request.HouseListRequest;
import com.ssafy.safeRent.recommend.dto.request.RecommendedHouseRequest;
import com.ssafy.safeRent.recommend.dto.response.HouseDetailResponse;
import com.ssafy.safeRent.recommend.dto.response.HouseListResponse;
import com.ssafy.safeRent.recommend.service.RecommendService;
import com.ssafy.safeRent.user.dto.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

  private final RecommendService recommendService;

  // 1. 매물 목록 조회
  @GetMapping("/list")
  public HouseListResponse getHouseList(@Valid @ModelAttribute HouseListRequest request) {
    return recommendService.getHouseList(request);
  }

  // 2. 매물 상세 조회
  @GetMapping("/{house_id}")
  public HouseDetailResponse getHouseDetail(@PathVariable("house_id") Long houseId) {
    return recommendService.getHouseDetail(houseId);
  }

//  // 3. 추천 매물 조회
//  @GetMapping("/recommended")
//  public HouseListResponse getRecommendedHouses(@Valid @ModelAttribute RecommendedHouseRequest request) {
//    try {
//      Infras.valueOf(request.getInfra().toUpperCase());
//    } catch (IllegalArgumentException e) {
//      throw new IllegalArgumentException(
//          "Invalid infra value: " + request.getInfra() + ". Must be one of: " + Infras.values());
//    }
//    return recommendService.getRecommendedHouses(request);
//  }

  // 4. 매물 관심목록 추가
  @PostMapping("/favorites")
  public ResponseEntity<?> addFavorite(@AuthenticationPrincipal User user, @RequestBody FavoriteHouseRequest request) {
    request.setUserId(user.getId());
    recommendService.addFavorite(request);
    return ResponseEntity.ok("추가 완료");
  }

}
