package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.domain.Mail;
import com.example.domain.Member;
import com.example.repository.MemberRepository;

@Service
public class SendEmailService {

	@Autowired private MemberRepository memberRepository;
	
	@Autowired private JavaMailSender mailSender;
	
	@Autowired private PasswordEncoder passwordEncoder;
	
	//메일생성및 패스워드 변경
	public Mail createMailAndChangePassword(String userEmail) {
		//임시패스워드 생성
		String str = getTempPassword();
		
		//송신할 메일정보 설정
		Mail mail = new Mail();
		mail.setAddress(userEmail);
		mail.setTitle("MintBook 임시비밀번호 안내메일");
		mail.setMessage("안녕하세요! MintBook 임시비밀번호 안내 메일입니다.\n"
		+ "임시비밀번호는 "+ str + " 입니다.\n"+"로그인 후 패스워드를 변경해주세요.");
		
		//임시패스워드로 패스워드 변경
		updatePassword(str,userEmail);
		return mail;
	}
	
	//패스워드 업데이트
	private void updatePassword(String str, String userEmail) {
		String pw = passwordEncoder.encode(str);
		
		Member memberOne = memberRepository.findByEmail(userEmail).get();
		
		memberOne.setPassword(pw);
		
		memberRepository.save(memberOne);
	}

	//임시 패스워드 생성
	private String getTempPassword() {
		char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		
		String str = "";

		int idx = 0;
		for(int i=0; i<10; i++) {
			idx = (int) (charSet.length * Math.random());
			str += charSet[idx];
		}
		
		return str;
	}
	
	//1개 메일전송
	public void mailSend(Mail mail) {
		System.out.println("이메일 전송 완료");
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getAddress());
		message.setSubject(mail.getTitle());
		message.setText(mail.getMessage());
		
		mailSender.send(message);
	}
	
	//alimi용 메일 생성
	public Mail createAlimiMail(String alimiWriterEmail, String bookName) {
		
		//송신할 메일정보 설정
		Mail mail = new Mail();
		mail.setAddress(alimiWriterEmail);
		mail.setTitle("MintBook 도서입고 안내");
		mail.setMessage("안녕하세요! MintBook 도서입고 안내 메일입니다.\n"
		+ bookName + "도서가 입고되었습니다.");
		
		return mail;
	}

	//여러개 메일 전송
	public void alimiMailSend(Mail mail) {
		System.out.println("이메일 전송 완료");
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getAddress());
		message.setSubject(mail.getTitle());
		message.setText(mail.getMessage());
		
		mailSender.send(message);
	}
}
