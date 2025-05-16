package com.ssafy.safeRent.board.dto.response;

import java.util.List;

import com.ssafy.safeRent.board.dto.model.Comment;

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
public class CommentListResponse {
  private List<Comment> commentList;
}