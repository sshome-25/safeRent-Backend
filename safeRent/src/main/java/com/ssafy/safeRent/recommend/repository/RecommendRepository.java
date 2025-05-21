package com.ssafy.safeRent.recommend.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ssafy.safeRent.recommend.dto.model.House;
import com.ssafy.safeRent.recommend.dto.request.RecommendedHouseRequest;
import com.ssafy.safeRent.recommend.dto.response.HouseDetailResponse;

@Mapper
public interface RecommendRepository {

        // 1. 매물 목록 조회
        // 현재 중심위치 기준으로 반경 500m 이내에 있는, 3개월 이내의 매물을 선택
        @Select("SELECT traded_house_id, CONCAT(aptNm, ' ', aptDong) AS name, price " +
                        "FROM traded_houses "
                        + "WHERE ST_Distance_Sphere(location, ST_PointFromText(CONCAT('POINT(',#{lat},' ',#{longi},')'),4326))<=500 "
                        + "AND transaction_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)")
        List<House> selectTradedHouses(@Param("lat") String lat, @Param("longi") String longi);

        // 2. 매물 상세 조회
        @Select("SELECT traded_house_id, CONCAT(aptNm, ' ', aptDong) AS name, "
                        + "price, area, built_year, floor, CONCAT(cityNm, ' ', umdNm, ' ', jibun) AS address "
                        + "FROM traded_houses WHERE traded_house_id = #{houseId}")
        HouseDetailResponse selectTradedHousesDetail(@Param("houseId") Long houseId);

        // 3-1. 특정 매물 위치 기준 반경 2KM 이내 인프라 수
        @Select("SELECT COUNT(*) AS infra_count FROM ${infra} "
                        + "WHERE ST_Distance_Sphere(location, ST_PointFromText(CONCAT('POINT(', #{lat}, ' ', #{longi}, ')'), 4326), 2000) ")
        Integer countInfraWithinRadius(RecommendedHouseRequest request);

        // 3-2. 특정 매물 위치에서 가장 가까운 인프라 거리
        @Select("SELECT ST_Distance_Sphere(location, ST_PointFromText(CONCAT('POINT(', #{lat}, ' ', #{longi}, ')'), 4326)) AS distance, name "
                        + "FROM ${infra} ORDER BY distance ASC LIMIT 1")
        Map<String, Object> findNearestInfra(RecommendedHouseRequest request);

        // 4. 매물 관심목록 추가
        @Insert("INSERT INTO favorites (user_id, traded_house_id) VALUES (#{userId}, #{houseId})")
        void insertFavorites(@Param("userId") Long userId, @Param("houseId") Long houseId);
}
