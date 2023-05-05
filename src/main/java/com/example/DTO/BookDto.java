package com.example.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookDto {
	private String bookName;
	private String author;
	private Integer price;
	private LocalDateTime regDate;
	private String isbn;
	private Integer hit;
	private Integer stock;
	private Integer genre;
	private String content;
	private String publisher;
	private String authorInfo;
	private LocalDate publishDate;
	private String bookImg;
}
