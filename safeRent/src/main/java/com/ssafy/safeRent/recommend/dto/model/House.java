package com.ssafy.safeRent.recommend.dto.model;

import java.time.LocalDate;

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
  private Double area;
  private Integer floor;
  private Integer builtYear;
  private LocalDate transactionDate;
  private String aptNm;
  private String aptDong;
  private String cityNm;
  private String umdNm;
  private String jibun;
}