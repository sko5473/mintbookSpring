package com.example.DTO;

import lombok.Data;

@Data
public class ReviewDto {
	private String content;
	private String writer;
	private Double star;
	private Integer bookId;
}
