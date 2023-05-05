package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Wishlist;

public interface WishListRepository  extends JpaRepository<Wishlist, Integer> {

	//찜 삭제
	Wishlist findByBookIdAndMemberId(Integer bookid, Integer memberid);
	
	//찜 삭제
	void deleteByBookIdAndMemberId(int bookid, int memberid);

	//내 찜목록
	Page<Wishlist> findByMemberId(int id, Pageable pageable);

}
