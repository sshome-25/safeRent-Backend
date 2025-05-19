package com.ssafy.safeRent.config;

import com.ssafy.safeRent.user.dto.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final Key key;
    
    // 토큰 유효 시간 (예: 24시간)
    private final long jwtExpirationMs;

    public JwtTokenUtil(@Value("${jwt.secret:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEF}") String secret,
                         @Value("${jwt.expiration:86400000}") long jwtExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // 사용자 이름으로부터 JWT 토큰 생성
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // JWT 토큰 생성
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰으로부터 사용자 이름 추출
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token, User user) {
        final String email = getEmailFromToken(token);
        return (email.equals(user.getEmail()) && !isTokenExpired(token));
    }
    
    // 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 토큰에서 만료 날짜 가져오기
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰에서 클레임 가져오기
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 모든 클레임 가져오기
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateGuestToken(String purpose, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "guest");
        claims.put("purpose", purpose);

        // 역할(권한) 정보 추가
        if (roles != null && roles.length > 0) {
            claims.put("roles", Arrays.asList(roles));
        } else {
            // 기본 게스트 권한만 부여
            claims.put("roles", Arrays.asList("ROLE_GUEST"));
        }

        return createToken(claims, "guest");
    }

    public Boolean validateGuestToken(Claims claims) {
        // 만료 시간 확인 (이미 parseClaimsJws에서 검증됨)
        // 추가 검증 로직 필요시 여기에 구현
        return true;
    }
}