package com.example.DTO;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class ReviewGetDto {
	private String content;
	private String bookname;
	private LocalDateTime regDate;
	private Double star;
}
