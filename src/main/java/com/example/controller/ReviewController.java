package com.example.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DTO.ReviewBookDto;
import com.example.DTO.ReviewDto;
import com.example.domain.Book;
import com.example.domain.Member;
import com.example.domain.Notice;
import com.example.domain.Review;
import com.example.security.SecurityUtil;
import com.example.service.BookService;
import com.example.service.MemberService;
import com.example.service.ReviewService;

@RestController
@RequestMapping("/api")
public class ReviewController {

	@Autowired ReviewService reviewService;
	
	@Autowired BookService bookService;
	
	@Autowired MemberService memberService;
	
	//리뷰 등록(Member테이블의 point(평점)도 update해준다)
	@PostMapping("/review")
	public ResponseEntity reviewAdd(@RequestBody ReviewDto params) {
		String email = SecurityUtil.getCurrentEmail();
		
		Book book= bookService.findById(params.getBookId());
		
		Member member = memberService.findByEmail(email);
		
		Review review = new Review();
		review.setContent(params.getContent());
		review.setWriter(email);
		review.setRegDate(LocalDateTime.now());
		review.setStar(params.getStar());
		review.setBook(book);
		review.setMember(member);
		
		reviewService.save(review);
		
		//리뷰 등록시 도서테이블의 평점을 갱신한다.
		Double avgStar = reviewService.getAverageStarRatingByBookId(params.getBookId());
		//평점 소수점 첫째자리 반올림
		avgStar = Math.round(avgStar * 10)/10.0;
		
		book.setStar(avgStar);
		bookService.save(book);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//리뷰 목록
	@GetMapping("/reviewall")
	public ResponseEntity reviewAll(@RequestParam("page") int page, @RequestParam("size") int size) {
	    Pageable pageable = PageRequest.of(page, size);

	    Page<Review> reviews = reviewService.findAll(pageable);
	    List<ReviewBookDto> reviewDtos = reviews.getContent().stream()
	            .map(r -> new ReviewBookDto(r.getId(), r.getContent(), r.getWriter(), r.getRegDate(),
	                    r.getStar(), r.getBook().getId(), r.getBook().getBookName(),r.getBook().getIsbn()))
	            .collect(Collectors.toList());
	    Page<ReviewBookDto> reviewpagedto = new PageImpl(reviewDtos, pageable, reviews.getTotalElements());

	    return new ResponseEntity<>(reviewpagedto, HttpStatus.OK);
	}
	
	//내 리뷰 목록
	@GetMapping("/myreviewall")
	public ResponseEntity myReviewAll(@RequestParam("page") int page, @RequestParam("size") int size) {
		String email = SecurityUtil.getCurrentEmail();
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Review> reviews = reviewService.findByWriter(email, pageable);
		
		List<ReviewBookDto> reviewDtos = reviews.getContent().stream()
	            .map(r -> new ReviewBookDto(r.getId(), r.getContent(), r.getWriter(), r.getRegDate(),
	                    r.getStar(), r.getBook().getId(), r.getBook().getBookName(),r.getBook().getIsbn()))
	            .collect(Collectors.toList());

		Page<ReviewBookDto> reviewpagedto = new PageImpl(reviewDtos, pageable, reviews.getTotalElements());
		
		return new ResponseEntity<>(reviewpagedto, HttpStatus.OK);
	}
	
	//책 상세페이지 리뷰 목록(최신순)
	@GetMapping("/onebookreviewall")
	public ResponseEntity oneBookReviewAll(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam int bookid) {
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Review> review = reviewService.findByBookIdOrderByRegDateDesc(bookid, pageable);
		
		return new ResponseEntity<>(review, HttpStatus.OK);
	}
	
	//책 상세페이지 리뷰 목록(별점순)
	@GetMapping("/onebookreviewallorderbystar")
	public ResponseEntity oneBookReviewAllOrderByStar(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam int bookid) {
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Review> review = reviewService.findByBookIdOrderByStarDesc(bookid, pageable);
		
		return new ResponseEntity<>(review, HttpStatus.OK);
	}
	
	//리뷰 1개 조회
	@GetMapping("/reviewone")
	public ResponseEntity myReivewOne(@RequestParam int id) {
	    Review review = reviewService.findById(id);
	    if (review == null) {
	        return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
	    }
	    ReviewBookDto reviewDto = new ReviewBookDto(review.getId(), review.getContent(), review.getWriter(),
	            review.getRegDate(), review.getStar(), review.getBook().getId(), review.getBook().getBookName(),
	            review.getBook().getIsbn());

	    return new ResponseEntity<>(reviewDto, HttpStatus.OK);
	}
	
	//리뷰 1개 수정
	@PutMapping("/review/{id}")
	public ResponseEntity reviewOneRevise(@PathVariable int id, @RequestBody ReviewDto params) {
		
		Review review = reviewService.findById(id);
		review.setStar(params.getStar());
		review.setContent(params.getContent());
		
		reviewService.save(review);
		
		Double avgStar = reviewService.getAverageStarRatingByBookId(params.getBookId());
		//평점 소수점 첫째자리 반올림
		avgStar = Math.round(avgStar * 10)/10.0;
		
		Book book= bookService.findById(params.getBookId());
		
		book.setStar(avgStar);
		bookService.save(book);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//1개 리뷰 삭제
	@DeleteMapping("/review/{id}")
	public ResponseEntity reviewDelete(@PathVariable int id) {
		reviewService.deleteById(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//리뷰 검색
	@GetMapping("/review/search")
	public ResponseEntity noticeSearch(@RequestParam("title") String title, @RequestParam("subject") String subject,
			@RequestParam int page, @RequestParam int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<ReviewBookDto> reviewpagedto = null;
		
		//검색분류가 작성자면 실행
		if(title.equals("writer")) {
			Page<Review> writerResult = reviewService.findByWriterContains(subject, pageable);
			System.out.println("작성자결과"+writerResult.getTotalElements());
			 List<ReviewBookDto> reviewBookDtos = writerResult.getContent().stream()
			            .map(r -> new ReviewBookDto(r.getId(), r.getContent(), r.getWriter(), r.getRegDate(),
			                    r.getStar(), r.getBook().getId(), r.getBook().getBookName(),r.getBook().getIsbn()))
			            .collect(Collectors.toList());
			 reviewpagedto = new PageImpl(reviewBookDtos, pageable, writerResult.getTotalElements());
		}
		//검색분류가 도서명이면 실행
		else if(title.equals("bookName")) {
			Page<Review> booknameResult = reviewService.findByBookBookNameContains(subject, pageable);
			System.out.println("북네임결과"+booknameResult);
			 List<ReviewBookDto> reviewBookDtos = booknameResult.getContent().stream()
			            .map(r -> new ReviewBookDto(r.getId(), r.getContent(), r.getWriter(), r.getRegDate(),
			                    r.getStar(), r.getBook().getId(), r.getBook().getBookName(),r.getBook().getIsbn()))
			            .collect(Collectors.toList());
			 
			 reviewpagedto = new PageImpl(reviewBookDtos, pageable, booknameResult.getTotalElements());
		}
		
		return new ResponseEntity<>(reviewpagedto, HttpStatus.OK);
	}
}
