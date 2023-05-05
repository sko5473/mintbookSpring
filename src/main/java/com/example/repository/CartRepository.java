package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Cart;
import com.example.domain.CartItem;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	Cart findByMemberId(int id);

	void save(CartItem newitem);

}
