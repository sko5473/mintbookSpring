package com.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DTO.BookDto;
import com.example.domain.Alimi;
import com.example.domain.Book;
import com.example.domain.Mail;
import com.example.service.AlimiService;
import com.example.service.BookService;
import com.example.service.SendEmailService;

@RestController
@RequestMapping("/api")
public class BookController {

	@Autowired BookService bookService;
	
	@Autowired SendEmailService sendEmailService;
	
	@Autowired AlimiService alimiService;
	
	//도서 등록
	@PostMapping("/book")
	public ResponseEntity bookAdd(@RequestBody BookDto params) {
		Book book = new Book();
		
		book.setBookName(params.getBookName());
		book.setAuthor(params.getAuthor());
		book.setPrice(params.getPrice());
		book.setRegDate(LocalDateTime.now());
		book.setIsbn(params.getIsbn());
		book.setStock(params.getStock());
		book.setGenre(params.getGenre());
		book.setStar(0.0);
		book.setContent(params.getContent());
		book.setPublisher(params.getPublisher());
		book.setAuthorInfo(params.getAuthorInfo());
		book.setPublishDate(params.getPublishDate());
		
		bookService.save(book);
		
		//도서 등록시 알리미에 등록되어있는 목록 중 동일한 isbn과 전송유무가 N인 작성자에게 입고 내용을 메일로 전송
		List<Alimi> alimi = alimiService.findByIsbnAndSendStatus(params.getIsbn(),"N");
		
		//알리미 대상 조회
		if(alimi == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		//대상자 한명씩 알림메일을 보낸다.
		for(var i=0; i<alimi.size(); i++) {
			String alimiWriterEmail = alimi.get(i).getWriter(); 
			
			Mail mail = sendEmailService.createAlimiMail(alimiWriterEmail, params.getBookName());
			try {
				sendEmailService.alimiMailSend(mail);

				//알림전송 완료되면 알림상태 Y로 갱신
				Alimi alimione = alimi.get(i);
				alimione.setSendStatus("Y");
				
				alimiService.save(alimione);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//도서 전체목록
	@GetMapping("/bookall")
	public ResponseEntity getBookAll(@RequestParam("page") int page, @RequestParam("size") int size) {
		Pageable pageable = PageRequest.of(page, size);

	    Page<Book> books = bookService.findAll(pageable);
	    
	    return new ResponseEntity<>(books, HttpStatus.OK);
	}
	
	//도서 전체 목록(판매량 순, 베스트셀러)
	@GetMapping("/bestsellerbookall")
	public ResponseEntity getBestsellerBookAll(@RequestParam("page") int page, @RequestParam("size") int size) {
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Book> books = bookService.findAllByOrderByHitDesc(pageable);
		
		return new ResponseEntity<>(books, HttpStatus.OK);
	}
	
	//도서검색(도서명, isbn, 저자명, 출판사)
	@GetMapping("/book/search")
	public ResponseEntity searchBook(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("keyword") String keyword, @RequestParam("content") String content) {
		
		Pageable pageable = PageRequest.of(page, size);
		Page<Book> searchResult = null;
		
		//키워드가 도서명일 때
		if(keyword.equals("bookName")) {
			searchResult = bookService.findByBookNameContains(content, pageable);
		}
		//키워드가 isbn일 때
		else if(keyword.equals("isbn")) {
			searchResult = bookService.findByIsbnContains(content, pageable);
		}
		//키워드가 author일 때
		else if(keyword.equals("author")) {
			searchResult = bookService.findByAuthorContains(content, pageable);
		}
		//키워드가 publisher일 때
		else if(keyword.equals("publisher")) {
			searchResult = bookService.findByPublisherContains(content, pageable);
		}
		
		return new ResponseEntity<>(searchResult, HttpStatus.OK);
	}
	
	//도서 1개 데이터 수신
	@GetMapping("/bookone")
	public ResponseEntity getOneBook(@RequestParam int id) {
		Book book = bookService.findById(id);
		return new ResponseEntity<>(book, HttpStatus.OK);
	}
	
	//도서 1개 데이터 수정
	@PutMapping("/book/{id}")
	public ResponseEntity bookReviseOne(@PathVariable int id, @RequestBody Book params) {
		
		Book book = bookService.findById(id);
		
		book.setBookName(params.getBookName());
		book.setAuthor(params.getAuthor());
		book.setPublisher(params.getPublisher());
		book.setPublishDate(params.getPublishDate());
		book.setPrice(params.getPrice());
		book.setIsbn(params.getIsbn());
		book.setStock(params.getStock());
		book.setGenre(params.getGenre());
		book.setAuthorInfo(params.getAuthorInfo());
		book.setContent(params.getContent());
		
		bookService.save(book);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
