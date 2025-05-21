package com.ssafy.safeRent.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.safeRent.board.dto.model.Comment;
import com.ssafy.safeRent.board.dto.model.Post;
import com.ssafy.safeRent.board.dto.request.CommentRequest;
import com.ssafy.safeRent.board.dto.request.PostRequest;
import com.ssafy.safeRent.board.dto.response.CommentListResponse;
import com.ssafy.safeRent.board.dto.response.PostListResponse;
import com.ssafy.safeRent.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;

  // 1. 게시글 목록 조회
  public PostListResponse getPostList(Integer page) {
    Integer offset = (page - 1) * 10; // 한 번에 보여줄 댓글 수 -> 10개
    List<Post> postList = boardRepository.findPosts(offset);
    return PostListResponse.builder()
        .postList(postList)
        .build();
  }

  // 2. 게시글 조회
  public Post getPost(Long postId) {
    Post post = boardRepository.findPostById(postId);
    if (post == null) {
      throw new IllegalArgumentException("Post not found with ID: " + postId);
    }
    return post;
  }

  // 3. 게시글 등록
  public void createPost(Long userId, PostRequest request) {

    Post post = Post.builder()
        .userId(userId)
        .tradedHouseId(request.getTradedHouseId())
        .title(request.getTitle())
        .content(request.getContent())
        .preferLocation(request.getPreferLocation())
        .preferRoomNum(request.getPreferRoomNum())
        .preferArea(request.getPreferArea())
        .isPark(request.getIsPark())
        .commentCount(request.getCommentCount())
        .build();
    boardRepository.insertPost(post);
  }

  // 4. 게시글 수정
  public void updatePost(Long userId, Long postId, PostRequest request) {
    Post existingPost = boardRepository.findPostById(postId);
    if (existingPost == null) {
      throw new IllegalArgumentException("Post not found with ID: " + postId);
    }
    if (!existingPost.getUserId().equals(userId)) {
      throw new IllegalStateException("User does not have permission to update this post");
    }
    Post post = Post.builder()
        .postId(postId)
        .title(request.getTitle())
        .content(request.getContent())
        .preferLocation(request.getPreferLocation())
        .preferRoomNum(request.getPreferRoomNum())
        .preferArea(request.getPreferArea())
        .isPark(request.getIsPark())
        .commentCount(request.getCommentCount())
        .userId(userId)
        .build();
    boardRepository.updatePost(post);
  }

  // 5. 게시글 삭제
  public void deletePost(Long userId, Long postId) {
    Post existingPost = boardRepository.findPostById(postId);
    if (existingPost == null) {
      throw new IllegalArgumentException("Post not found with ID: " + postId);
    }
    if (!existingPost.getUserId().equals(userId)) {
      throw new IllegalStateException("User does not have permission to delete this post");
    }
    boardRepository.deletePost(postId);
  }

  // 6. 댓글 목록 조회
  public CommentListResponse getCommentList(Long postId) {
    List<Comment> commentList = boardRepository.findCommentsByPostId(postId);
    return CommentListResponse.builder()
        .commentList(commentList)
        .build();
  }

  // 7. 댓글 등록
  public void createComment(Long userId, CommentRequest request) {
    Comment comment = Comment.builder()
        .parentCommentId(request.getParentCommentId())
        .content(request.getContent())
        .houseId(request.getHouseId())
        .postId(request.getPostId())
        .userId(userId)
        .build();
    boardRepository.insertComment(comment);
  }

  // 8. 댓글 수정
  public void updateComment(Long userId, Long commentId, CommentRequest request) {
    Comment existingComment = boardRepository.findCommentById(commentId);
    if (existingComment == null) {
      throw new IllegalArgumentException("Comment not found with ID: " + commentId);
    }
    if (!existingComment.getUserId().equals(userId)) {
      throw new IllegalStateException("User does not have permission to update this comment");
    }
    Comment comment = Comment.builder()
        .commentId(commentId)
        .parentCommentId(request.getParentCommentId())
        .content(request.getContent())
        .houseId(request.getHouseId())
        .postId(existingComment.getPostId())
        .userId(userId)
        .build();
    boardRepository.updateComment(comment);
  }

  // 9. 댓글 삭제
  public void deleteComment(Long userId, Long commentId) {
    Comment existingComment = boardRepository.findCommentById(commentId);
    if (existingComment == null) {
      throw new IllegalArgumentException("Comment not found with ID: " + commentId);
    }
    if (!existingComment.getUserId().equals(userId)) {
      throw new IllegalStateException("User does not have permission to delete this comment");
    }
    boardRepository.deleteComment(commentId);
  }
}