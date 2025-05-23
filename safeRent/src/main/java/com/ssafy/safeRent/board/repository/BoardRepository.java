package com.ssafy.safeRent.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ssafy.safeRent.board.dto.model.Comment;
import com.ssafy.safeRent.board.dto.model.Post;

@Mapper
public interface BoardRepository {

        // 1. 게시글 목록 조회
        @Select("SELECT p.post_id, p.title, p.view_count, p.prefer_location, p.prefer_room_num, p.prefer_area, p.is_park, "
                        +
                        "p.content, p.created_at, u.nickname AS authorNickname, COUNT(c.comment_id) AS commentCount, p.user_id, "
                        +
                        "p.traded_house_id " +
                        "FROM posts p JOIN users u ON p.user_id = u.user_id " +
                        "LEFT JOIN comments c ON p.post_id = c.post_id " +
                        "WHERE p.status = 'ACTIVE' AND "
                        + "CASE "
                        + "WHEN #{category} = 'all' THEN TRUE "
                        + "ELSE p.category = #{category} "
                        + "END " +
                        "GROUP BY p.post_id " +
                        "ORDER BY p.${orderBy} DESC " +
                        "LIMIT 10 OFFSET #{offset}")
        List<Post> findPosts(@Param("offset") Integer offset, @Param("category") String category, @Param("orderBy") String orderBy);

        // 2. 게시글 조회
        @Select("SELECT p.post_id, p.title, p.view_count, p.prefer_location, p.prefer_room_num, p.prefer_area, p.is_park, "
                        +
                        "p.content, p.created_at, u.nickname AS authorNickname, COUNT(c.comment_id) AS commentCount, p.user_id "
                        +
                        "FROM posts p JOIN users u ON p.user_id = u.user_id " +
                        "LEFT JOIN comments c ON p.post_id = c.post_id " +
                        "WHERE p.status = 'ACTIVE' AND p.post_id = #{postId} " +
                        "GROUP BY p.post_id")
        Post findPostById(Long postId);

        @Update("UPDATE posts SET view_count = view_count + 1 WHERE post_id = #{postId}")
        void increaseViewCount(Long postId);

        // 3. 게시글 등록
        @Insert("INSERT INTO posts (user_id, traded_house_id, title, content, prefer_location, prefer_room_num, " +
                        "prefer_area, is_park, category) " +
                        "VALUES (#{userId}, #{tradedHouseId}, #{title}, #{content}, #{preferLocation}, " +
                        "#{preferRoomNum}, #{preferArea}, #{isPark}, #{category})")
        @Options(useGeneratedKeys = true, keyProperty = "postId")
        void insertPost(Post post);

        // 4. 게시글 수정
        @Update("UPDATE posts SET content = #{content}, title = #{title}, prefer_location = #{preferLocation}, " +
                        "prefer_room_num = #{preferRoomNum}, prefer_area = #{preferArea}, is_park = #{isPark} " +
                        "WHERE post_id = #{postId}")
        void updatePost(Post post);

        // 5. 게시글 삭제
        @Update("UPDATE posts SET status = 'INACTIVE' WHERE post_id = #{postId}")
        void deletePost(@Param("postId") Long postId);

        // 6. 댓글 목록 조회
        @Select("SELECT c.comment_id, c.parent_comment_id, c.content, c.created_at, c.updated_at, users.nickname AS authorNickname, "
                        +
                        "c.post_id, c.user_id " +
                        "FROM comments c JOIN users ON users.user_id = c.user_id " +
                        "WHERE c.post_id = #{postId} and c.status = 'ACTIVE' ")
        List<Comment> findCommentsByPostId(Long postId);

        // 7. 댓글 등록
        @Insert("INSERT INTO comments (user_id, post_id, content) " +
                        "VALUES (#{userId}, #{postId}, #{content})")
        @Options(useGeneratedKeys = true, keyProperty = "commentId")
        void insertComment(Comment comment);

        // 8. 댓글 수정
        @Update("UPDATE comments SET parent_comment_id = #{parentCommentId}, content = #{content}, " +
                        "traded_house_id = #{houseId} " +
                        "WHERE comment_id = #{commentId}")
        void updateComment(Comment comment);

        // 9. 댓글 삭제
        @Update("UPDATE comments SET status = 'INACTIVE' WHERE comment_id = #{commentId}")
        void deleteComment(Long commentId);

        // 댓글 단건 조회 (내부 메서드)
        @Select("SELECT comment_id, parent_comment_id, content, traded_house_id, u.nickname AS authorNickname, " +
                        "c.created_at, post_id, c.user_id " +
                        "FROM comments c JOIN users u ON c.user_id = u.user_id " +
                        "WHERE comment_id = #{commentId}")
        Comment findCommentById(Long commentId);
}