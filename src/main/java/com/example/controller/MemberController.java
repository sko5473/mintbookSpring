package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DTO.MemberLoginRequestDto;
import com.example.DTO.MemberReviseDto;
import com.example.DTO.MemberRevisePwDto;
import com.example.DTO.TokenInfo;
import com.example.domain.Member;
import com.example.security.SecurityUtil;
import com.example.security.RefreshToken;
import com.example.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@CrossOrigin("*")
public class MemberController {
	
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    
    //로그인
    @PostMapping("/login")
    public TokenInfo login(@RequestBody MemberLoginRequestDto memberLoginRequestDto, HttpServletResponse res) {
        String email = memberLoginRequestDto.getEmail();
        String password = memberLoginRequestDto.getPassword();
        
        TokenInfo tokenInfo = memberService.login(email, password);
        
        //access토큰 cookie에 추가
		Cookie accessCookie = new Cookie("accessToken", tokenInfo.getAccessToken());
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		
		//refresh토큰 cookie에 추가
		Cookie refreshCookie = new Cookie("refreshToken", tokenInfo.getRefreshToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		
		res.addCookie(accessCookie);
		res.addCookie(refreshCookie);
		
        return tokenInfo;
    }
    
    //Sns로그인(sns연동계정 확인 된 후 실행되는 로직)
    @PostMapping("/snslogin")
    public TokenInfo snsLogin(@RequestBody Map<String, String> params, HttpServletResponse res) {
    	String snsId = params.get("snsId");
    	Member member = memberService.findBySnsId(snsId);
    	
        String email = member.getEmail();
        String password = "snsjoin";
        
        TokenInfo tokenInfo = memberService.snsLogin(email, password, member);
        
        //access토큰 cookie에 추가
		Cookie accessCookie = new Cookie("accessToken", tokenInfo.getAccessToken());
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		
		//refresh토큰 cookie에 추가
		Cookie refreshCookie = new Cookie("refreshToken", tokenInfo.getRefreshToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		
		res.addCookie(accessCookie);
		res.addCookie(refreshCookie);
		
        return tokenInfo;
    }
    
    //로그아웃
	@PostMapping("/logout")
	public ResponseEntity logout(HttpServletResponse res) {
		Cookie accessCookie = new Cookie("accessToken", null);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(0);
		
		Cookie refreshCookie = new Cookie("refreshToken", null);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(0);
		
		res.addCookie(accessCookie);
		res.addCookie(refreshCookie);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
    //회원가입
    @PostMapping("/join")
    public ResponseEntity join(@RequestBody Member params) throws Exception {
    	return new ResponseEntity<>(memberService.save(params), HttpStatus.OK);
    }
    
    //SNS회원가입
    @PostMapping("/snsjoin")
    public ResponseEntity snsJoin(@RequestBody Member params) throws Exception {
    	return new ResponseEntity<>(memberService.snsSave(params), HttpStatus.OK);
    }
    
    //회원수정(일반데이터)
    @PutMapping("/user")
    public ResponseEntity reviseMember(@RequestBody MemberReviseDto params) throws Exception {
    	String email = SecurityUtil.getCurrentEmail();
    	
    	Member memberOne = memberService.findByEmail(email);
    	memberOne.setGender(params.getGender());
    	memberOne.setAddress(params.getAddress());
    	memberOne.setAddDetail(params.getAddDetail());
    	memberOne.setAge(params.getAge());
    	memberOne.setName(params.getName());
    	memberOne.setPhone(params.getPhone());
    	memberOne.setBirth(params.getBirth());
    	
    	memberService.revise(memberOne);
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    //회원수정(패스워드)
    @PutMapping("/userpw")
    public ResponseEntity reviseMemberPw(@RequestBody MemberRevisePwDto params) throws Exception {
    	String email = SecurityUtil.getCurrentEmail();
    	
    	Member memberOne = memberService.findByEmail(email);
    	
    	//현재 패스워드와 입력된 패스워드가 일치하면 패스워드 변경작업을 실행
    	if(passwordEncoder.matches(params.getPrePassword(), memberOne.getPassword())) {
    		memberOne.setPassword(passwordEncoder.encode(params.getChangePassword()));
    		
    		memberService.revise(memberOne);
    	}
    
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    //RTK검증완료시 ATK를 재발행하는 요청
    @PostMapping("/refreshaccesstoken")
    public ResponseEntity<TokenInfo> refresh(@RequestBody TokenInfo token) throws Exception {
    	return new ResponseEntity<TokenInfo>(memberService.refreshAccessTokenGen(token), HttpStatus.OK);
    }
    
    //RTK검증요청
    @PostMapping("/refreshcheck")
    public ResponseEntity<RefreshToken> refreshcheck(@RequestBody TokenInfo token) throws Exception {
    	return new ResponseEntity<RefreshToken>(memberService.validRefreshToken(token), HttpStatus.OK);
    }
    
    //SNS로그인시 연동계정 유무 확인
    @PostMapping("/snscheck")
    public ResponseEntity snsCheck(@RequestBody Map<String,String> params) {
    	
    	String snsId = params.get("snsId");
    	Member member = memberService.findBySnsId(snsId);
    	
    	String result;
    	
    	//조회된 내용 기준 true/false를 반환
    	if(member != null) {
    		result = "true";
    	} else {
    		result = "false";
    	}
    	return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    //회원가입 이메일 중복 체크
    @GetMapping("/emailcheck")
    public ResponseEntity emailCheck(@RequestParam String email) {
    	
    	int result = memberService.countByEmail(email);

    	return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    //로그인 검증
    @PostMapping("/logincheck")
    public ResponseEntity loginCheck() {
    	String email = SecurityUtil.getCurrentEmail();
    	
    	Member result = memberService.findByEmail(email);
    	
    	return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    //Admin권한체크
    @PostMapping("/admincheck")
    public ResponseEntity<Boolean> adminCheck() {
		String email = SecurityUtil.getCurrentEmail();
    	
    	Member member = memberService.findByEmail(email);
    	
    	String role = member.getRoles().get(0);
    	Boolean result;
    	
    	if(role.equals("ADMIN")) {
    		result = true;
    	} else {
    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}
    	
    	return new ResponseEntity<>(result, HttpStatus.OK);
    }
}