package com.ssafy.safeRent.board.dto.response;

import java.util.List;

import com.ssafy.safeRent.board.dto.model.Post;

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
public class PostListResponse {
  private List<Post> postList;
}