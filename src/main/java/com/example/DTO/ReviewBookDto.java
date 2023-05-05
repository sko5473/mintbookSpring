package com.example.DTO;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewBookDto {
	private int reviewid;
	
	private String content;
	
	private String writer;
	
	private LocalDateTime regDate;
	
	private Double star;
	
	private Integer bookId;
	
	private String bookName;
	
	private String isbn;
}
