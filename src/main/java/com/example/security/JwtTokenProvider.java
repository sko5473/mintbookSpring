package com.example.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.DTO.TokenInfo;
import com.example.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author LKH
 * 토큰 생성, 토큰복호화 및 정보 추출, 토큰 유효성 검증
 */

@Slf4j
@Component
public class JwtTokenProvider {

	@Autowired private RefreshTokenRepository refreshTokenRepository;
	
	//JWT KEY
	private final Key key;
	
	//  Base64로 인코딩된 문자열 형태의 secretKey를 디코딩하여 대칭키(key)를 생성하는 코드.
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}
	
	// 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(Authentication authentication) {
    	
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        long now = (new Date()).getTime();
        
        // Access Token 만료시간
        Date accessTokenExpiresIn = new Date(now + 6000000); //1000: 1초(밀리초)
        
        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) //email
                .claim("auth", authorities) //ROLE(ADMIN OR USER)
                .setExpiration(accessTokenExpiresIn) //토큰 만료시간
                .signWith(key, SignatureAlgorithm.HS256) //암호화
                .compact(); //생성
        
        // Refresh Token 생성
        String refreshToken = createRefreshToken(authentication.getName());
        
        
        //생성정보 리턴
        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(authorities)
                .build();
    }
	
    //Refresh Token 생성
    public String createRefreshToken(String email) {
    	//Redis에 저장하고 유효기간이 만료되면 데이터가 삭제된다.
        RefreshToken token = refreshTokenRepository.save(
                RefreshToken.builder()
                        .id(email) //이메일
                        .refresh_token(UUID.randomUUID().toString())
                        .expiration(6000) // 토큰만료기간(초)100분 뒤
                        .build()
        );
        return token.getRefresh_token();
    }
    
    //JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
        
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        
        // UserDetails 객체를 만들어서 권한정보(Authentication) 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
 
	// access 토큰 생성(refresh토큰으로 검증 후 재 생성시 사용)
    public String createToken(String account, List<String> authorities) {
        Claims claims = Jwts.claims().setSubject(account);

        claims.put("auth", authorities);
        
        long now = (new Date()).getTime();
        
        //Access Token 만료기한
        Date accessTokenExpiresIn = new Date(now + 6000000);//1000: 1초

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date()) //발행시간
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
        	//key로 검증
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
 
    // 토큰에 담겨있는 유저 account(email) 획득
    public String getAccount(String token) {
        // 만료된 토큰에 대해 parseClaimsJws를 수행하면 io.jsonwebtoken.ExpiredJwtException이 발생한다.
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            //토큰 만료시에도 email정보를 리턴한다.
            return e.getClaims().getSubject(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
    
    // 토큰 복호화
    private Claims parseClaims(String accessToken) {
        try {
        	//토큰정보 리턴
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
