package com.ssafy.safeRent.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.safeRent.user.dto.model.User;
import com.ssafy.safeRent.user.dto.request.SignupRequest;
import com.ssafy.safeRent.user.dto.response.UserResponse;
import com.ssafy.safeRent.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
    public UserResponse registerUser(SignupRequest signupRequest) throws Exception {
        // 이메일 또는 사용자 이름이 이미 존재하는지 확인
        if (userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new RuntimeException("Nickname is already taken");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // 새 사용자 생성
        User user = User.builder()
        		.email(signupRequest.getEmail())
        		.password(passwordEncoder.encode(signupRequest.getPassword()))
        		.nickname(signupRequest.getNickname())
        		.build();
        
        // 사용자 저장
       Long userId = userRepository.save(user);
        
       if (userId <= 0) {
           throw new Exception("Failed to save user");
       }
        // 응답 객체 생성
        UserResponse userResponse = UserResponse.builder()
        		.id(userId)
        		.email(user.getEmail())
        		.nickname(user.getNickname())
        		.build();
        
        return userResponse;
    }

}
