package com.example.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author LKH 
 * 인증이 필요할때 실행되는 클래스
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
 
    private final JwtTokenProvider jwtTokenProvider;
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
    	Cookie[] cookies = httpRequest.getCookies();
    	String accessToken = null;
    	
    	if(cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("accessToken")) {
	            	// 1. 쿠키에서 accessToken 추출
	                accessToken = cookie.getValue();
	            }
	        }
	        // 2. validateToken메서드로 토큰 유효성 검사
	        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
	            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
	            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        } 
    	}
        //다음 필터링 진행
        chain.doFilter(request, response);
    }
}
