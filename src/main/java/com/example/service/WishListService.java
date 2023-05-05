package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Wishlist;
import com.example.repository.WishListRepository;

@Service
public class WishListService {

	@Autowired WishListRepository wishListRepository;

	//찜 추가
	public Wishlist save(Wishlist wishlist) {
		return wishListRepository.save(wishlist);
	}

	public Wishlist findByBookIdAndMemberId(Integer bookid, Integer memberid) {
		return wishListRepository.findByBookIdAndMemberId(bookid, memberid);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteByBookIdAndMemberId(int bookid, int memberid) {
		wishListRepository.deleteByBookIdAndMemberId(bookid, memberid);
	}

	public Page<Wishlist> findByMemberId(int id, Pageable pageable) {
		return wishListRepository.findByMemberId(id, pageable);
	}
}
