package com.ssafy.safeRent.board.dto.model;

import java.time.LocalDateTime;

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
public class Comment {
  @JsonProperty("comment_id")
  private Long commentId;

  @JsonProperty("parent_comment_id")
  private Long parentCommentId;

  @JsonProperty("house_id")
  private Long houseId;

  @JsonProperty("content")
  private String content;

  @JsonProperty("author_nickname")
  private String authorNickname;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("post_id")
  private Long postId; // 등록/수정 시 사용

  @JsonProperty("user_id")
  private Long userId; // 등록/수정 시 사용
}