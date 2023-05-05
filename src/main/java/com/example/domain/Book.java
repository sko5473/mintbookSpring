package com.example.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "book")
@ToString(exclude = "reviews")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 255)
	private String bookName;
	
	@Column(length = 100)
	private String author;
	
	private Integer price;
	
	private LocalDateTime regDate;
	
	@Column(length = 255,unique = true)
	private String isbn;
	
	private Integer hit; //판매량
	
	private Integer stock; //재고수
	
	private Integer genre; //카테고리
	
	private Double star; //평점
	
	private String content; //줄거리
	
	private String publisher; //출판사
	
	private String authorInfo; //작가정보
	
	private LocalDate publishDate; //출판일
	
	private String bookImg; //책 표지 URL
	
	@JsonBackReference(value="book-reviews")
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Review> reviews=new ArrayList<>();
	
	@JsonBackReference(value="book-editers")
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<EditerPick> editers=new ArrayList<>();
	
	@JsonBackReference(value="book-eventbook")
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<EventBook> eventbooks=new ArrayList<>();
	
	@JsonBackReference(value="book-wishlists")
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Wishlist> wishlists=new ArrayList<>();
	
	@JsonBackReference(value="book-cartitems")
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
	private List<CartItem> cartitems=new ArrayList<>();
	
	@JsonBackReference(value="book-orderitems")
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
	private List<OrderItem> orderitems=new ArrayList<>();
}