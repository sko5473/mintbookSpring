package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Cart;
import com.example.domain.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

	//카트아이템 저장
	void save(Cart newcart);

	//카트아이템에 데이터 조회
	CartItem findByCartIdAndBookId(Integer cartid, Integer bookid);
	
}
