package com.example.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.example.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    
    private String fappid = "2692732430895830";
    
    private String fapppw = "eb9685d36509657507e1996502bda9a3";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
//    	HttpServletRequest httpRequest = (HttpServletRequest) request;
//    	Cookie[] cookies = httpRequest.getCookies();
//    	String accessToken = null;
//    	
//    	if(cookies != null) {
//	        for (Cookie cookie : cookies) {
//	            if (cookie.getName().equals("accessToken")) {
//	            	// 1. 쿠키에서 accessToken 추출
//	                accessToken = cookie.getValue();
//	            }
//	        }
//	        // 2. validateToken메서드로 토큰 유효성 검사
//	        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
//	            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
//	            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//	            SecurityContextHolder.getContext().setAuthentication(authentication);
//	        } 
//    	}
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
        String faccesstoken = httpRequest.getHeader("Authorization");
        System.out.println("인증헤더"+faccesstoken);
    	
        if (faccesstoken != null) {
    	String url = "http://graph.facebook.com/debug_token";
    	
           // axios요청시마다 헤더값에서 accessToken값을 받아와서 페이스북api로 토큰검증을 한다.
            String encodedFaccesstoken = URLEncoder.encode(faccesstoken, "UTF-8");
            String queryParameters = String.format("input_token=%s&access_token=%s", encodedFaccesstoken, fappid + "|" + fapppw);
            String urlString = url + "?" + queryParameters;

            URL requestUrl = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String redirectUrl = connection.getHeaderField("Location");
                System.out.println("리다이렉션 URL: " + redirectUrl);

                // 리다이렉션된 URL로 다시 요청 보내기
                URL redirectRequestUrl = new URL(redirectUrl);
                HttpURLConnection redirectConnection = (HttpURLConnection) redirectRequestUrl.openConnection();
                redirectConnection.setRequestMethod("GET");

                int redirectResponseCode = redirectConnection.getResponseCode();
                if (redirectResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(redirectConnection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    String result = responseBuilder.toString();
                    
                    // result 문자열을 JSON으로 파싱
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(result);
                    
                    //토큰유효한지 확인
                    boolean isValid = jsonNode.path("data").path("is_valid").asBoolean();
                    System.out.println("is_valid: " + isValid);
                    System.out.println("결과1: " + result);
                    
                    String snsid = jsonNode.path("data").path("user_id").asText();
                    System.out.println("snsid이"+snsid);
                    
                    if (isValid == true) {
                    	// 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
        	            Authentication authentication = jwtTokenProvider.fgetAuthentication(snsid);
        	            SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                String result = responseBuilder.toString();
                System.out.println("결과2: " + result);
            } 
        }
        
        //다음 필터링 진행
        chain.doFilter(request, response);
    }
}
