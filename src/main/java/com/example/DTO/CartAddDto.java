package com.example.DTO;

import lombok.Data;

@Data
public class CartAddDto {
	private Integer memberid;
	private Integer[] bookid;
}
