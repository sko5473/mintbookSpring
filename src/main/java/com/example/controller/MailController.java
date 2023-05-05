package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Mail;
import com.example.service.MemberService;
import com.example.service.SendEmailService;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin("*")
public class MailController {

	@Autowired SendEmailService sendEmailService;
	
	@Autowired MemberService memberService;
	
	//패스워드 찾기시 입력된 메일로 가입된 이력이 있는지 확인
	@GetMapping("/checkemail")
	public Map<String, Boolean> checkEmail(@RequestParam String userEmail){
		Map<String, Boolean> result = new HashMap<>();
		boolean emailCheck = memberService.userEmailCheck(userEmail);
		
		result.put("check", emailCheck);
		
		return result;
	}
	
	//패스워드 찾기(메일 임시 암호발송)
	@GetMapping("/sendemailpw")
	public ResponseEntity sendEmailPw(@RequestParam String userEmail) {
		
		Mail mail = sendEmailService.createMailAndChangePassword(userEmail);
		
		//입력받은 사용자 정보를 토대로 메일 전송
		sendEmailService.mailSend(mail);
		
		return new ResponseEntity<>(mail, HttpStatus.OK);
	}
}
