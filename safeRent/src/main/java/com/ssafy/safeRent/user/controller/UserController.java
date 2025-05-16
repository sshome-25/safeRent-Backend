package com.ssafy.safeRent.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.safeRent.auth.dto.JwtRequest;
import com.ssafy.safeRent.auth.dto.JwtResponse;
import com.ssafy.safeRent.config.JwtTokenUtil;
import com.ssafy.safeRent.user.dto.model.User;
import com.ssafy.safeRent.user.dto.request.SignupRequest;
import com.ssafy.safeRent.user.dto.response.UserResponse;
import com.ssafy.safeRent.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

	private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
    	try {
    		String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        	signupRequest.setPassword(encodedPassword);
    		UserResponse userResponse = userService.registerUser(signupRequest);    
        	
            return ResponseEntity.ok(userResponse);
    	} catch (Exception e) {
    		// 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "회원가입 처리 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final User user = (User) userService
                .loadUserByUsername(authenticationRequest.getEmail());

        final String token = jwtTokenUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new JwtResponse(token));
    }
    
    private void authenticate(String nickname, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(nickname, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    
    @GetMapping("/testToken")
    public ResponseEntity<?> testToken() {
    	System.out.println("성공");
    	return ResponseEntity.ok().body("success");
    }
}
