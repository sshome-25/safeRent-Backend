package com.ssafy.safeRent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ssafy.safeRent.user.dto.model.User;
import com.ssafy.safeRent.user.dto.request.SignupRequest;
import com.ssafy.safeRent.user.dto.response.UserResponse;
import com.ssafy.safeRent.user.repository.UserRepository;
import com.ssafy.safeRent.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private SignupRequest validSignupRequest;
    private User savedUser;
    private final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        // 유효한 회원가입 요청 설정
        validSignupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testUser")
                .build();

        // 저장된 사용자 설정
        savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password(ENCODED_PASSWORD)
                .nickname("testUser")
                .build();
    }

    @Test
    @DisplayName("유효한 사용자 등록 - 성공")
    void registerUser_WithValidRequest_ReturnsUserResponse() throws Exception {
        // Given
        when(userRepository.existsByNickname("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            userArg.setId(1L); // ID 설정
            return 1L; // 성공적으로 저장되었음을 나타내는 값
        });

        // When
        UserResponse userResponse = userService.registerUser(validSignupRequest);

        // Then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isEqualTo(1L);
        assertThat(userResponse.getEmail()).isEqualTo("test@example.com");
        assertThat(userResponse.getNickname()).isEqualTo("testUser");

        // 메서드 호출 검증
        verify(userRepository).existsByNickname("testUser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 등록 시도 - 실패")
    void registerUser_WithExistingNickname_ThrowsException() {
        // Given
        when(userRepository.existsByNickname("testUser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(validSignupRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Nickname is already taken");
        
        // 메서드 호출 검증
        verify(userRepository).existsByNickname("testUser");
        verify(userRepository, times(0)).existsByEmail(any()); // 이 메서드는 호출되지 않아야 함
        verify(userRepository, times(0)).save(any()); // 이 메서드는 호출되지 않아야 함
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 등록 시도 - 실패")
    void registerUser_WithExistingEmail_ThrowsException() {
        // Given
        when(userRepository.existsByNickname("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(validSignupRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Email is already in use");
        
        // 메서드 호출 검증
        verify(userRepository).existsByNickname("testUser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, times(0)).save(any()); // 이 메서드는 호출되지 않아야 함
    }

    @Test
    @DisplayName("사용자 저장 실패 - 예외 발생")
    void registerUser_SaveFails_ThrowsException() {
        // Given
        when(userRepository.existsByNickname("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(0L); // 저장 실패

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(validSignupRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Failed to save user");
        
        // 메서드 호출 검증
        verify(userRepository).existsByNickname("testUser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 인코딩 확인")
    void registerUser_EncodesPassword() throws Exception {
        // Given
        when(userRepository.existsByNickname("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            userArg.setId(1L);
            return 1L;
        });

        // When
        userService.registerUser(validSignupRequest);

        // Then
        verify(passwordEncoder).encode("password123");
        
        // 인코딩된 비밀번호가 사용자 객체에 설정되었는지 확인
        verify(userRepository).save(any(User.class));
    }
}