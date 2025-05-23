package com.ssafy.safeRent.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.safeRent.board.dto.model.Post;
import com.ssafy.safeRent.board.dto.request.CommentRequest;
import com.ssafy.safeRent.board.dto.request.PostRequest;
import com.ssafy.safeRent.board.dto.response.CommentListResponse;
import com.ssafy.safeRent.board.dto.response.PostListResponse;
import com.ssafy.safeRent.board.service.BoardService;
import com.ssafy.safeRent.user.dto.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/boards")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
    RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS }, allowCredentials = "true")
public class BoardController {

  private final BoardService boardService;

  // 1. 게시글 목록 조회
  @GetMapping
  public ResponseEntity<?> getPostList(@RequestParam("page") Integer page, @RequestParam("category") String category) {
	System.out.println(category);
    PostListResponse postListResponse = boardService.getPostList(page, category);
    return ResponseEntity.ok().body(postListResponse);
  }

  // 2. 게시글 조회
  @GetMapping("/{post_id}")
  public ResponseEntity<?> getPost(@PathVariable("post_id") Long postId) {
    Post post = boardService.getPost(postId);
    return ResponseEntity.ok().body(post);
  }

  // 3. 게시글 등록
  @PostMapping
  public ResponseEntity<?> createPost(@AuthenticationPrincipal User user, @Valid @RequestBody PostRequest request) {
    boardService.createPost(user.getId(), request);
    return ResponseEntity.ok("등록 완료");
  }

  // 4. 게시글 수정
  @PatchMapping("/{post_id}")
  public ResponseEntity<?> updatePost(@AuthenticationPrincipal User user, @PathVariable("post_id") Long postId,
      @Valid @RequestBody PostRequest request) {
    boardService.updatePost(user.getId(), postId, request);
    return ResponseEntity.ok("수정 완료");
  }

  // 5. 게시글 삭제
  @DeleteMapping("/{post_id}")
  public ResponseEntity<?> deletePost(@AuthenticationPrincipal User user, @PathVariable("post_id") Long postId) {
    boardService.deletePost(user.getId(), postId);
    return ResponseEntity.ok("삭제 완료");
  }

  // 6. 댓글 목록 조회
  @GetMapping("/comments")
  public ResponseEntity<?> getCommentList(@RequestParam("post_id") Long postId) {
    CommentListResponse commentListResponse = boardService.getCommentList(postId);
    return ResponseEntity.ok().body(commentListResponse);
  }

  // 7. 댓글 등록
  @PostMapping("/comments")
  public ResponseEntity<?> createComment(@AuthenticationPrincipal User user,
      @Valid @RequestBody CommentRequest request) {
    boardService.createComment(user.getId(), request);
    System.out.println("as");
    return ResponseEntity.ok("등록 완료");
  }

  // 8. 댓글 수정
  @PatchMapping("/comments/{comment_id}")
  public ResponseEntity<?> updateComment(@AuthenticationPrincipal User user,
      @PathVariable("comment_id") Long commentId, @Valid @RequestBody CommentRequest request) {
    boardService.updateComment(user.getId(), commentId, request);
    return ResponseEntity.ok("수정 완료");
  }

  // 9. 댓글 삭제
  @DeleteMapping("/comments/{comment_id}")
  public ResponseEntity<?> deleteComment(@AuthenticationPrincipal User user,
      @PathVariable("comment_id") Long commentId) {
    boardService.deleteComment(user.getId(), commentId);
    return ResponseEntity.ok("삭제 완료");
  }
}