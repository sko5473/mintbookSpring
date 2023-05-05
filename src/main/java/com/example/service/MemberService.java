package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.DTO.TokenInfo;
import com.example.domain.Member;
import com.example.repository.MemberRepository;
import com.example.repository.RefreshTokenRepository;
import com.example.security.JwtTokenProvider;
import com.example.security.RefreshToken;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	//일반로그인
	@Transactional
	public TokenInfo login(String email, String password) {
		// 1. Login ID/PW 를 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
		
		TokenInfo tokenInfo;
		
		// 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		
		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		tokenInfo = jwtTokenProvider.generateToken(authentication);
		
		return tokenInfo;
	}
	
	//sns로그인
	@Transactional
	public TokenInfo snsLogin(String email, String password, Member member) {
			
		Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
		
		List<SimpleGrantedAuthority> roles = new ArrayList<>();
		
		for (GrantedAuthority authority : authorities) {
		    roles.add(new SimpleGrantedAuthority(authority.getAuthority()));
		}
		
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, roles);
        
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
		
		return tokenInfo;
	}
	
	//회원가입
	public boolean save(Member params) throws Exception {
		try {
			Member member = Member.builder()
					.email(params.getEmail())
					.password(passwordEncoder.encode(params.getPassword()))
					.gender(params.getGender())
					.address(params.getAddress())
					.addDetail(params.getAddDetail())
					.age(params.getAge())
					.name(params.getName())
					.phone(params.getPhone())
					.birth(params.getBirth())
					.joinDate(LocalDateTime.now())
					.point(0)
					.build();
			member.setRoles(Collections.singletonList("USER")); //로그인하는 가입자에게 기본으로 USER권한 부여
			
			memberRepository.save(member);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception("잘못된 회원가입 요청입니다.");
		}
		return true;
	}

	//SNS회원가입
	public boolean snsSave(Member params) throws Exception {
		try {
			Member member = Member.builder()
					.email(params.getEmail())
					.password(passwordEncoder.encode("snsjoin")) //sns회원가입일 경우 패스워드는 임시로 snsjoin을 부여
					.gender(params.getGender())
					.address(params.getAddress())
					.addDetail(params.getAddDetail())
					.age(params.getAge())
					.name(params.getName())
					.phone(params.getPhone())
					.birth(params.getBirth())
					.joinDate(LocalDateTime.now())
					.snsConnectDate(LocalDateTime.now())
					.snsId(params.getSnsId())
					.snsType(params.getSnsType())
					.snsProfile(params.getSnsProfile())
					.point(0)
					.build();
			member.setRoles(Collections.singletonList("USER")); //로그인하는 가입자에게 기본으로 USER권한 부여
			
			memberRepository.save(member);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception("잘못된 회원가입 요청입니다.");
		}
		return true;
	}
		
	//RTK검증
	public RefreshToken validRefreshToken(TokenInfo tokens) throws Exception {
		//토큰에 저장되어있는 email(subject)조회
		String email = jwtTokenProvider.getAccount(tokens.getAccessToken());
		
		//조회된 email로 member Entity에서 유저정보 조회
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new BadCredentialsException("잘못된 계정정보입니다."));
		
		//조회된 유저정보의 email로 Redis에 저장되어있는 RefreshToken조회
		RefreshToken refreshToken = refreshTokenRepository.findById(member.getEmail())
				.orElseThrow(() -> new Exception("만료된 계정입니다. 로그인을 다시 시도하세요"));
		
		// 해당유저의 Refresh 토큰 만료 : Redis에 해당 유저의 토큰이 존재하지 않음
		if (refreshToken.getRefresh_token() == null) {
			return null; //없으면 null
		} else {
			//있으면 refreshToken 리턴
			return refreshToken;
		}
	}
    
	//ATK토큰 재발행
	public TokenInfo refreshAccessTokenGen(TokenInfo token) throws Exception {
		//토큰에서 email조회
		String email = jwtTokenProvider.getAccount(token.getAccessToken());
		
		//조회된 email로 Member조회
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new BadCredentialsException("잘못된 계정정보입니다."));
		
		//Member email로 refresh토큰 조회
		RefreshToken refreshToken = refreshTokenRepository.findById(member.getEmail())
				.orElseThrow(() -> new Exception("만료된 계정입니다. 로그인을 다시 시도하세요"));
			
		//토큰정보 리턴시 ATK재생성
		return TokenInfo.builder().accessToken(jwtTokenProvider.createToken(email, member.getRoles()))
				.refreshToken(refreshToken.getRefresh_token()).build();
	}

	//이메일로 유저정보 조회
	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email).get();
	}

	//회원정보 수정
	public void revise(Member memberOne) {
		memberRepository.save(memberOne);
	}

	//SNS로그인시 연동계정 유무 확인
	public Member findBySnsId(String snsId) {
		return memberRepository.findBySnsId(snsId);
	}

	//회원가입 이메일 중복체크
	public int countByEmail(String email) {
		return memberRepository.countByEmail(email);
	}

	public boolean userEmailCheck(String userEmail) {
		Optional<Member> member = memberRepository.findByEmail(userEmail);
		
		if(member.isEmpty()) {
			return false;
		}
		else { 
			return true;
		}
	}
}