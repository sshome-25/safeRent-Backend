package com.ssafy.safeRent.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.safeRent.config.SecurityConfig;
import com.ssafy.safeRent.user.controller.UserController;
import com.ssafy.safeRent.user.dto.request.SignupRequest;
import com.ssafy.safeRent.user.dto.response.UserResponse;
import com.ssafy.safeRent.user.service.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)  // Security 설정 가져오기
public class ControllerTest {

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean  // 여기 취소선 제거
    private UserService userService;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())  // Spring Security 설정 적용
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void testSignupSuccess() throws Exception {
        // Given
        SignupRequest signupRequest = SignupRequest.builder()
                .nickname("testuser")
                .password("password123")
                .email("test@example.com")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testnick")
                .build();

        // When
        when(userService.registerUser(any(SignupRequest.class))).thenReturn(userResponse);

        // Then
        try {
            mockMvc.perform(post("/api/user/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.nickname").value("testnick"));
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 서비스 예외 발생")
    public void testSignupFailure() throws Exception {
        // Given
        SignupRequest signupRequest = SignupRequest.builder()
                .nickname("testuser")
                .password("password123")
                .email("test@example.com")
                .build();

        // When
        when(userService.registerUser(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("이미 존재하는 사용자입니다."));

        // Then
        try {
            mockMvc.perform(post("/api/user/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isInternalServerError());        	
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
		}
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 잘못된 이메일 형식")
    public void testSignupInvalidRequest() throws Exception {
        // Given
        SignupRequest invalidRequest = SignupRequest.builder()
                .nickname("testuser")
                .password("password123")
                .email("notEmail")
                .build();

        // Then
        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}