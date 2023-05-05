package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.DTO.CartAddDto;
import com.example.domain.Book;
import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.Member;
import com.example.security.SecurityUtil;
import com.example.service.BookService;
import com.example.service.CartItemService;
import com.example.service.CartService;
import com.example.service.MemberService;

@RestController
@RequestMapping("/api")
public class CartController {

	@Autowired CartService cartService;
	
	@Autowired MemberService memberService;
	
	@Autowired CartItemService cartItemService;
	
	@Autowired BookService bookService;
	
	//카트 추가
	@PostMapping("/cart")
	public ResponseEntity addCart(@RequestBody List<Integer> bookids) {
		
		String email = SecurityUtil.getCurrentEmail();
		Member member = memberService.findByEmail(email);
		
		Cart cart = cartService.findByMemberId(member.getId());
		
		//카트 조회시 카트가 null이면 카트부터 저장해준다.
		if(cart == null) {
			Cart newcart = new Cart();
			newcart.setMember(member);
			
			cartService.save(newcart);
			cart = cartService.findByMemberId(member.getId());
		}
		
		//배열로 받아온 bookid값을 for문으로 꺼내서 저장한다.
	    for (int i = 0; i < bookids.size(); i++) {
	        Integer bookid = bookids.get(i);
	        CartItem cartitem = cartItemService.findByCartIdAndBookId(cart.getId(),bookid);
	        Book book = bookService.findById(bookid);
	        
	      //카트아이템이 없으면 새로 저장
			if(cartitem == null) {
				CartItem newitem = new CartItem();
				newitem.setBook(book);
				newitem.setCart(cart);
				newitem.setQuantity(1);
				cartItemService.save(newitem);
			} else {
				//기존등록정보가 있으면 기존 수량 +1
				cartitem.setQuantity(cartitem.getQuantity()+1);
				cartItemService.save(cartitem);
			}
	    }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
