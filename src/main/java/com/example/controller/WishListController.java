package com.example.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DTO.ReviewBookDto;
import com.example.DTO.WishlistBookDto;
import com.example.domain.Book;
import com.example.domain.Member;
import com.example.domain.Review;
import com.example.domain.Wishlist;
import com.example.security.SecurityUtil;
import com.example.service.BookService;
import com.example.service.MemberService;
import com.example.service.WishListService;

@RestController
@RequestMapping("/api")
public class WishListController {

	@Autowired WishListService wishListService;
	@Autowired MemberService memberService;
	@Autowired BookService bookService;
	
	//찜 추가
	@PostMapping("/wishlist")
	public ResponseEntity addWishList(@RequestParam("id") int bookid) {
		String email = SecurityUtil.getCurrentEmail();
		Member member = memberService.findByEmail(email);
		Book book = bookService.findById(bookid);
		
		Wishlist wishlist = new Wishlist();
		wishlist.setMember(member);
		wishlist.setBook(book);
		
		Wishlist addwishlistdata = wishListService.save(wishlist);
		
		return new ResponseEntity<>(addwishlistdata, HttpStatus.OK);
	}
	
	//찜 삭제
	@DeleteMapping("/wishlist")
	public ResponseEntity deleteWishList(@RequestParam("id") Integer bookid) {
		String email = SecurityUtil.getCurrentEmail();
		Member member = memberService.findByEmail(email);
		wishListService.deleteByBookIdAndMemberId(bookid, member.getId());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//찜 유무 조회
	@GetMapping("/checkwishlist")
	public ResponseEntity checkWishList(@RequestParam("id") int bookid) {
		String email = SecurityUtil.getCurrentEmail();
		Member member = memberService.findByEmail(email);
		Wishlist  wishlist = wishListService.findByBookIdAndMemberId(bookid, member.getId());
		
		Boolean result = null;
		if(wishlist == null) {
			result = false; //찜 되어 있지 않음
		} else {
			result = true; //찜이 되어있음.
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	//찜 목록 조회
	@GetMapping("/allwishlist")
	public ResponseEntity allWishList(@RequestParam("page") int page, @RequestParam("size") int size) {
		String email = SecurityUtil.getCurrentEmail();
		Member member = memberService.findByEmail(email);
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Wishlist> mywishlist = wishListService.findByMemberId(member.getId(), pageable);
		
		List<WishlistBookDto> wishlistdto = mywishlist.getContent().stream()
				.map(w -> new WishlistBookDto(w.getBook().getId(), w.getBook().getBookImg(), w.getBook().getBookName(), w.getBook().getAuthor(), w.getBook().getPrice(), w.getBook().getPublishDate()))
				.collect(Collectors.toList());
		Page<WishlistBookDto> wishlistpagedto = new PageImpl(wishlistdto, pageable, mywishlist.getTotalElements());
		
		return new ResponseEntity<>(wishlistpagedto, HttpStatus.OK);
		
	}
}
