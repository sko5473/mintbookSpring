package com.example.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlimiDto {
	private String bookName; //도서명
	
	private String isbn; //isbn
	
	private String writer; //작성자
	
	private LocalDateTime regDate; //등록일
	
	private Boolean sendStatus; //알림여부
}
