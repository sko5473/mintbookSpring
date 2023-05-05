package com.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Mail {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	//메일 주소
	private String address;
	
	//메일 제목
	private String title;
	
	//메일 내용
	private String message;

}
