package com.ssafy.safeRent.board.repository;

import static com.ssafy.safeRent.jooq.generated.Tables.*;

import com.ssafy.safeRent.board.dto.model.Post;
import com.ssafy.safeRent.jooq.generated.enums.CommentsStatus;
import com.ssafy.safeRent.jooq.generated.enums.PostsStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
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

}
