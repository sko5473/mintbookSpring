package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/***
 * 
 * @author LKH
 * 스프링 시큐리티 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
 
    private final JwtTokenProvider jwtTokenProvider;
 
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() //Http basic auth 기반으로 출력되는 로그인 인증창을 disable하여 막음
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                	//해당페이지(로그인, 회원가입,ATK재발급, RTK검증)는 인증없이 접근 허용됨
	                .requestMatchers("/api/members/login","/api/members/join","/api/members/refreshaccesstoken","/api/members/refreshcheck",
	                		"/api/members/snscheck","/api/members/snslogin","/api/members/snsjoin","/api/members/emailcheck",
	                		"/api/members/logout","/api/mail/sendemailpw","/api/mail/checkemail","/api/bestsellerbookall",
	                		"api/members/fsnslogincheck","/api/members/fsnslogin").permitAll() 
	                .requestMatchers("/api/admin/*").hasRole("ADMIN") //해당 페이지는 ADMIN권한 가진 유저만 접근됨
	                .anyRequest().authenticated() //그 외 모든 요청에 대해 인증이 필요함.
	            .and()
	            //UsernamePasswordAuthenticationFilter인증 전에 JwtAuthenticationFilter(jwtTokenProvider)를 먼저 처리함.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

