package com.example.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	//제목
	private String title;

	//내용
	private String content;
	
	//작성자
	private String writer;
	
	//작성일자
	private LocalDateTime regDate;
	
	//수정일자
	private LocalDateTime reviseDate;
	
}
