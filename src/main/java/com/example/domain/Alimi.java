package com.example.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Alimi {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int alimiId; //인덱스
	
	private String bookName; //도서명
	
	private String isbn; //isbn
	
	private String writer; //작성자
	
	private LocalDateTime regDate; //등록일
	
	private String sendStatus; //알림여부
}
