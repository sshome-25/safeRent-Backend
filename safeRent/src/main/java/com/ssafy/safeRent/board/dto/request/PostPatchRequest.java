package com.ssafy.safeRent.board.dto.request;

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
public class PostPatchRequest {
	  private String title;
	  private String content;
	  @JsonProperty("prefer_location")
	  private String preferLocation;

	  @JsonProperty("prefer_room_num")
	  private Integer preferRoomNum;

	  @JsonProperty("prefer_area")
	  private Double preferArea;

	  @JsonProperty("is_park")
	  private Boolean isPark;

}
