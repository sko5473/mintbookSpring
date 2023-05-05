package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.CartItem;
import com.example.repository.CartItemRepository;

@Service
public class CartItemService {

	@Autowired CartItemRepository cartItemRepository;

	public CartItem findByCartIdAndBookId(Integer cartid, Integer  bookid) {
		return cartItemRepository.findByCartIdAndBookId(cartid, bookid);
	}

	public void save(CartItem newitem) {
		cartItemRepository.save(newitem);
	}
}
