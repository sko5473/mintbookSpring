package com.example.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WishlistBookDto {

	private Integer bookId;
	
	private String bookImg;
	
	private String bookName;
	
	private String author;
	
	private Integer price;
	
	private LocalDate publishDate;
}
