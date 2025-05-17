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
public class CommentRequest {
  // 등록/수정 공통
  @JsonProperty("parent_comment_id")
  private Long parentCommentId;

  @JsonProperty("content")
  private String content;

  @JsonProperty("house_id")
  private Long houseId;

  @JsonProperty("post_id")
  private Long postId; // 등록 시 사용
}