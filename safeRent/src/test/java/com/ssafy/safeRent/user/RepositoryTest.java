package com.ssafy.safeRent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.ssafy.safeRent.user.dto.model.User;
import com.ssafy.safeRent.user.repository.UserRepository;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE) // 실제 MySQL 데이터베이스 사용
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    void setUp() {
        // 테스트 실행 전에 users 테이블 비우기
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
        
        // 테스트 데이터 추가
        jdbcTemplate.execute(
                "INSERT INTO users (password, email, nickname) " +
                "VALUES ('password123', 'existing@example.com', 'existingNick')"
        );
    }
    
    @AfterEach
    void checkDatabaseState() {
        // 각 테스트 후 데이터베이스 상태 확인
        List<Map<String, Object>> allUsers = jdbcTemplate.queryForList("SELECT * FROM users");
        System.out.println("Database state after test: " + allUsers);
    }
    
    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하는 경우")
    void existsByNickname_WithExistingNickname_ReturnsTrue() {
        // When
        boolean exists = userRepository.existsByNickname("existingNick");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하지 않는 경우")
    void existsByNickname_WithNonExistingNickname_ReturnsFalse() {
        // When
        boolean exists = userRepository.existsByNickname("nonExistingNick");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
    void existsByEmail_WithExistingEmail_ReturnsTrue() {
        // When
        boolean exists = userRepository.existsByEmail("existing@example.com");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
    void existsByEmail_WithNonExistingEmail_ReturnsFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    @DisplayName("사용자 저장 및 ID 생성 확인")
    void save_ValidUser_SavesAndReturnsGeneratedId() {
        // Given
        User user = User.builder()
        		.password("password123")
        		.email("newuser@example.com")
        		.nickname("newUserNick")
        		.build();

        // When
        Long savedId = userRepository.save(user);
        
        // Then
        assertThat(savedId).isGreaterThan(0);
        assertThat(user.getId()).isGreaterThan(0);
                
        // 저장된 사용자 확인
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", 
                "user_id = " + user.getId() + " AND nickname = 'newUserNick'");
        assertThat(count).isEqualTo(1);
    }
    
    @Test
    @DisplayName("중복 닉네임으로 저장 시도 - 예외 발생")
    void save_WithDuplicateNickname_ThrowsException() {
        // Given
        User user = User.builder()
        		.password("password123")
        		.email("newuser@example.com")
        		.nickname("existingNick")
        		.build();
        
        // When & Then
        try {
            userRepository.save(user);
            // 예외가 발생해야 하므로 여기에 도달하면 테스트 실패
            assertThat(false).isTrue();
        } catch (Exception e) {
            // 예외가 발생하면 테스트 성공
            assertThat(e).isNotNull();
        }
    }
    
    @Test
    @DisplayName("중복 이메일로 저장 시도 - 예외 발생")
    void save_WithDuplicateEmail_ThrowsException() {
        // Given
        User user = User.builder()
        		.password("password123")
        		.email("existing@example.com")
        		.nickname("newUserNick")
        		.build();
        
        // When & Then
        try {
            userRepository.save(user);
            // 예외가 발생해야 하므로 여기에 도달하면 테스트 실패
            assertThat(false).isTrue();
        } catch (Exception e) {
            // 예외가 발생하면 테스트 성공
            assertThat(e).isNotNull();
        }
    }
}