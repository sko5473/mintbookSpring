package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

	//도서명 검색
	Page<Book> findByBookNameContains(String content, Pageable pageable);

	//isbn 검색
	Page<Book> findByIsbnContains(String content, Pageable pageable);

	//저자명 검색
	Page<Book> findByAuthorContains(String content, Pageable pageable);

	//출판사 검색
	Page<Book> findByPublisherContains(String content, Pageable pageable);

	//베스트셀러 검색
	Page<Book> findAllByOrderByHitDesc(Pageable pageable);

	Book findById(Integer[] bookid);

}
