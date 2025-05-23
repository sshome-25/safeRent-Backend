package com.ssafy.safeRent.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class UserService implements UserDetailsService {
	
	private final UserRepository userRepository;

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
        		.password(signupRequest.getPassword())
        		.nickname(signupRequest.getNickname())
        		.build();
        
        // 사용자 저장
       Long userId = userRepository.save(user);
        
       if (userId <= 0) {
           throw new Exception("Failed to save user");
       }
        // 응답 객체 생성
        UserResponse userResponse = UserResponse.builder()
        		.id(user.getId())
        		.email(user.getEmail())
        		.nickname(user.getNickname())
        		.build();
        
        return userResponse;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		return User.builder()
			.id(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.password(user.getPassword())
			.status(user.getStatus())
			.build();
	}

}
