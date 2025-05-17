package com.ssafy.safeRent.board.dto.model;

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
public class Post {
  @JsonProperty("post_id")
  private Long postId;

  @JsonProperty("title")
  private String title;

  @JsonProperty("author_nickname")
  private String authorNickname;

  @JsonProperty("prefer_location")
  private String preferLocation;

  @JsonProperty("prefer_room_num")
  private Integer preferRoomNum;

  @JsonProperty("prefer_area")
  private Double preferArea;

  @JsonProperty("is_park")
  private Boolean isPark;

  @JsonProperty("content")
  private String content;

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("comment_count")
  private Integer commentCount;

  @JsonProperty("user_id")
  private Long userId; // 등록/수정 시 사용

  @JsonProperty("traded_house_id")
  private Long tradedHouseId; // 등록/수정 시 사용
}