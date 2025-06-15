package com.ssafy.safeRent.board;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.safeRent.board.repository.BoardJooqRepository;
import com.ssafy.safeRent.board.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE) // 실제 MySQL 데이터베이스 사용
class RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(RepositoryTest.class);
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardJooqRepository boardJooqRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전에 테이블 비우기
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
            "comments", "posts", "users", "roles"
        );

        // 테스트 데이터 추가
        jdbcTemplate.execute(
            "INSERT INTO roles (role_id, name) VALUES (1, 'admin'), (2, 'user')"
        );

        jdbcTemplate.execute(
            "INSERT INTO users (user_id, email, password, nickname, role_id, status) " +
                "VALUES (1, 'test1@example.com', 'password123', 'testUser1', 2, 'ACTIVE'), " +
                "       (2, 'test2@example.com', 'password456', 'testUser2', 2, 'ACTIVE')"
        );
    }

    @Test
    @DisplayName("전체 카테고리 조회 테스트 - category='all'")
    void findPosts_AllCategory() {
        // Given
        jdbcTemplate.execute(
            "INSERT INTO posts (user_id, title, content, category, prefer_location, prefer_room_num, prefer_area, is_park, status) " +
                "VALUES (1, 'EXCHANGE 게시글', '교환 내용', 'EXCHANGE', '강남구', 3, 84.5, true, 'ACTIVE'), " +
                "       (1, 'SALE 게시글', '판매 내용', 'SALE', '서초구', 4, 100.0, false, 'ACTIVE'), " +
                "       (2, 'RENT 게시글', '임대 내용', 'RENT', '송파구', 2, 60.0, true, 'ACTIVE')"
        );

        // When
        var posts = boardJooqRepository.findPosts(0, "all", "created_at");

        // Then
        assertThat(posts).hasSize(3);
        assertThat(posts).extracting("category")
            .contains("EXCHANGE", "SALE", "RENT");
        assertThat(posts).extracting("authorNickname")
            .contains("testUser1", "testUser2");
    }

    @Test
    @DisplayName("정상적인 게시글 조회 테스트")
    void findPostById_Success() {
        // Given
        jdbcTemplate.execute(
            "INSERT INTO posts (post_id, user_id, title, content, category, view_count, " +
                "prefer_location, prefer_room_num, prefer_area, is_park, status) " +
                "VALUES (100, 1, '테스트 게시글', '테스트 내용입니다.', 'EXCHANGE', 5, " +
                "'강남구', 3, 84.5, true, 'ACTIVE')"
        );

        // When
        var post = boardRepository.findPostById(100L);

        // Then
        assertThat(post).isNotNull();
        assertThat(post.getPostId()).isEqualTo(100L);
        assertThat(post.getTitle()).isEqualTo("테스트 게시글");
        assertThat(post.getContent()).isEqualTo("테스트 내용입니다.");
        assertThat(post.getPreferLocation()).isEqualTo("강남구");
        assertThat(post.getPreferRoomNum()).isEqualTo(3);
        assertThat(post.getPreferArea()).isEqualTo(84.5);
        assertThat(post.getIsPark()).isTrue();
        assertThat(post.getUserId()).isEqualTo(1L);
        assertThat(post.getAuthorNickname()).isEqualTo("testUser1");
        assertThat(post.getCommentCount()).isZero();
    }
}
