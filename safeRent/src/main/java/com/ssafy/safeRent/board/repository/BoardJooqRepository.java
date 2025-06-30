package com.ssafy.safeRent.board.repository;

import static com.ssafy.safeRent.jooq.generated.Tables.*;

import com.ssafy.safeRent.board.dto.model.Comment;
import com.ssafy.safeRent.board.dto.model.Post;
import com.ssafy.safeRent.jooq.generated.enums.CommentsStatus;
import com.ssafy.safeRent.jooq.generated.enums.PostsStatus;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.types.UByte;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardJooqRepository {

    private final DSLContext dsl;

    public List<Post> findPosts(Integer offset, String category, String orderBy) {

        // 동적 ORDER BY 필드 결정
        Field<?> orderByField = getOrderByField(orderBy);

        // 댓글 수 계산을 위한 서브쿼리
        Field<Integer> commentCount = dsl.select(org.jooq.impl.DSL.count())
            .from(COMMENTS)
            .where(COMMENTS.POST_ID.eq(POSTS.POST_ID))
            .and(COMMENTS.STATUS.eq(CommentsStatus.ACTIVE))
            .asField("commentCount");

        var query = dsl.select(
                POSTS.POST_ID,
                POSTS.TITLE,
                POSTS.VIEW_COUNT,
                POSTS.PREFER_LOCATION,
                POSTS.PREFER_ROOM_NUM,
                POSTS.PREFER_AREA,
                POSTS.IS_PARK,
                POSTS.CONTENT,
                POSTS.CREATED_AT,
                USERS.NICKNAME.as("authorNickname"),
                commentCount.as("commentCount"),
                POSTS.USER_ID,
                POSTS.TRADED_HOUSE_ID,
                POSTS.CATEGORY
            )
            .from(POSTS)
            .join(USERS).on(POSTS.USER_ID.eq(USERS.USER_ID))
            .where(POSTS.STATUS.eq(PostsStatus.ACTIVE));

        // 카테고리 필터 적용
        if (!"all".equals(category)) {
            query = query.and(POSTS.CATEGORY.eq(category));
        }

        return query.orderBy(orderByField.desc())
            .limit(10)
            .offset(offset)
            .fetchInto(Post.class);
    }

    private Field<?> getOrderByField(String orderBy) {
        return switch (orderBy) {
            case "view_count" -> POSTS.VIEW_COUNT;
            case "created_at" -> POSTS.CREATED_AT;
            default -> POSTS.CREATED_AT;
        };
    }

    public Post findPostById(Long postId) {
        return dsl.select(
                POSTS.POST_ID,
                POSTS.TITLE,
                POSTS.VIEW_COUNT,
                POSTS.PREFER_LOCATION,
                POSTS.PREFER_ROOM_NUM,
                POSTS.PREFER_AREA,
                POSTS.IS_PARK,
                POSTS.CONTENT,
                POSTS.CREATED_AT,
                USERS.NICKNAME.as("authorNickname"),
                org.jooq.impl.DSL.count(COMMENTS.COMMENT_ID).as("commentCount"),
                POSTS.USER_ID
            )
            .from(POSTS)
            .join(USERS).on(POSTS.USER_ID.eq(USERS.USER_ID))
            .leftJoin(COMMENTS).on(POSTS.POST_ID.eq(COMMENTS.POST_ID)
                .and(COMMENTS.STATUS.eq(CommentsStatus.ACTIVE))) // 활성 댓글만 JOIN
            .where(POSTS.STATUS.eq(PostsStatus.ACTIVE))
            .and(POSTS.POST_ID.eq(postId))
            .groupBy(POSTS.POST_ID)
            .fetchOneInto(Post.class);
    }

    public void increaseViewCount(Long postId) {
        dsl.update(POSTS)
            .set(POSTS.VIEW_COUNT, POSTS.VIEW_COUNT.add(1))
            .where(POSTS.POST_ID.eq(postId))
            .execute();
    }

    public void insertPost(Post post) {
        Long generatedId = dsl.insertInto(POSTS)
            .set(POSTS.USER_ID, post.getUserId())
            .set(POSTS.TRADED_HOUSE_ID, post.getTradedHouseId())
            .set(POSTS.TITLE, post.getTitle())
            .set(POSTS.CONTENT, post.getContent())
            .set(POSTS.PREFER_LOCATION, post.getPreferLocation())
            .set(POSTS.PREFER_ROOM_NUM, UByte.valueOf(post.getPreferRoomNum()))
            .set(POSTS.PREFER_AREA, BigDecimal.valueOf(post.getPreferArea()))
            .set(POSTS.IS_PARK, post.getIsPark() != null ? (byte) (post.getIsPark() ? 1 : 0) : null)
            .set(POSTS.CATEGORY, post.getCategory())
            .returning(POSTS.POST_ID)  // 내부적으로 JDBC Generated Keys 사용
            .fetchOne(POSTS.POST_ID);

        post.setPostId(generatedId);
    }

    public void updatePost(Post post) {
        dsl.update(POSTS)
            .set(POSTS.CONTENT, post.getContent())
            .set(POSTS.TITLE, post.getTitle())
            .set(POSTS.PREFER_LOCATION, post.getPreferLocation())
            .set(POSTS.PREFER_ROOM_NUM, UByte.valueOf(post.getPreferRoomNum()))
            .set(POSTS.PREFER_AREA, BigDecimal.valueOf(post.getPreferArea()))
            .set(POSTS.IS_PARK, post.getIsPark() != null ? (byte) (post.getIsPark() ? 1 : 0) : null)
            .where(POSTS.POST_ID.eq(post.getPostId()))
            .execute();
    }

    public void deletePost(@Param("postId") Long postId) {
        dsl.update(POSTS)
            .set(POSTS.STATUS, PostsStatus.INACTIVE)
            .where(POSTS.POST_ID.eq(postId))
            .execute();
    }

    public List<Comment> findCommentsByPostId(Long postId) {
        return dsl.select(
                COMMENTS.COMMENT_ID,
                COMMENTS.PARENT_COMMENT_ID,
                COMMENTS.CONTENT,
                COMMENTS.CREATED_AT,
                COMMENTS.UPDATED_AT,
                USERS.NICKNAME.as("authorNickname"),
                COMMENTS.POST_ID,
                COMMENTS.USER_ID
            )
            .from(COMMENTS)
            .join(USERS).on(COMMENTS.USER_ID.eq(USERS.USER_ID))
            .where(COMMENTS.POST_ID.eq(postId))
            .and(COMMENTS.STATUS.eq(CommentsStatus.ACTIVE))
            .fetchInto(Comment.class);
    }

    public void insertComment(Comment comment) {
        Long generatedId = dsl.insertInto(COMMENTS)
            .set(COMMENTS.USER_ID, comment.getUserId())
            .set(COMMENTS.POST_ID, comment.getPostId())
            .set(COMMENTS.CONTENT, comment.getContent())
            .returning(COMMENTS.COMMENT_ID)  // 내부적으로 JDBC Generated Keys 사용
            .fetchOne(COMMENTS.COMMENT_ID);

        comment.setCommentId(generatedId);
    }

    public void updateComment(Comment comment) {
        dsl.update(COMMENTS)
            .set(COMMENTS.PARENT_COMMENT_ID, comment.getParentCommentId())
            .set(COMMENTS.CONTENT, comment.getContent())
            .where(COMMENTS.COMMENT_ID.eq(comment.getCommentId()))
            .execute();
    }

    public void deleteComment(Long commentId) {
        dsl.update(COMMENTS)
            .set(COMMENTS.STATUS, CommentsStatus.INACTIVE)
            .where(COMMENTS.COMMENT_ID.eq(commentId))
            .execute();
    }

    public Comment findCommentById(Long commentId) {
        return dsl.select(
                COMMENTS.COMMENT_ID,
                COMMENTS.PARENT_COMMENT_ID,
                COMMENTS.CONTENT,
                COMMENTS.CREATED_AT,
                COMMENTS.UPDATED_AT,
                USERS.NICKNAME.as("authorNickname"),
                COMMENTS.POST_ID,
                COMMENTS.USER_ID
            )
            .from(COMMENTS)
            .join(USERS).on(COMMENTS.USER_ID.eq(USERS.USER_ID))
            .where(COMMENTS.COMMENT_ID.eq(commentId))
            .fetchOneInto(Comment.class);
    }
}
