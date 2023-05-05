package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.repository.CartItemRepository;
import com.example.repository.CartRepository;

@Service
public class CartService {

	@Autowired CartRepository cartRepository;
	
	@Autowired CartItemRepository cartItemRepository;
	
	public Cart findByMemberId(int id) {
		return cartRepository.findByMemberId(id);
	}

	//카트아이템 저장
	public void save(CartItem newitem) {
		cartRepository.save(newitem);
	}

	//카트저장
	public void save(Cart newcart) {
		cartItemRepository.save(newcart);
	}

	
}
