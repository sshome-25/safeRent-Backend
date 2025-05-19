package com.ssafy.safeRent.config;

import com.ssafy.safeRent.user.service.UserService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.safeRent.user.dto.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String email = null;
        String jwtToken = null;

        // JWT 토큰은 "Bearer token" 형식으로 전달됨
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                // 토큰에서 claims 추출
                Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);

                // 게스트 토큰인지 확인
                if (claims.containsKey("type") && "guest".equals(claims.get("type"))) {
                    // 게스트 토큰 처리
                    handleGuestToken(claims, request);
                } else {
                    // 일반 사용자 토큰 처리
                    email = jwtTokenUtil.getEmailFromToken(jwtToken);

                    // 토큰이 유효하고 인증이 아직 설정되지 않은 경우 인증 설정
                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = (User) this.userService.loadUserByUsername(email);
                        if (jwtTokenUtil.validateToken(jwtToken, user)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());
                            authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token has expired");
            } catch (JwtException e) {
                logger.warn("Invalid JWT token");
            }
        } else {
            logger.debug("JWT Token does not begin with Bearer String or is null");
        }

        chain.doFilter(request, response);
    }

    /**
     * 게스트 토큰 처리 메소드
     */
    private void handleGuestToken(Claims claims, HttpServletRequest request) {
        // 인증이 아직 설정되지 않은 경우에만 처리
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // 토큰이 유효한지 확인 (만료 여부는 이미 예외 처리됨)
            if (jwtTokenUtil.validateGuestToken(claims)) {
                // 게스트 권한 설정
                Collection<SimpleGrantedAuthority> authorities;

                // claims에 roles가 있으면 사용, 없으면 기본 ROLE_GUEST 부여
                if (claims.containsKey("roles")) {
                    List<String> roles = (List<String>) claims.get("roles");
                    authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                } else {
                    authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_GUEST"));
                }

                // 게스트 사용자 인증 객체 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                        "guest", null, authorities);

                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                logger.info("Guest authentication successful with token purpose: " +
                    (claims.containsKey("purpose") ? claims.get("purpose") : "not specified"));
            }
        }
    }
}