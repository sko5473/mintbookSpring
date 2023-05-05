package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.domain.Book;
import com.example.repository.BookRepository;

@Service
public class BookService {

	@Autowired BookRepository bookRepository;

	//도서검색
	public Book findById(Integer bookId) {
		return bookRepository.findById(bookId).get();
	}
	
	//도서 등록
	public void save(Book book) {
		bookRepository.save(book);
	}

	public Page<Book> findAll(Pageable pageable) {
		return bookRepository.findAll(pageable);
	}

	public Page<Book> findByBookNameContains(String content, Pageable pageable) {
		return bookRepository.findByBookNameContains(content, pageable);
	}

	public Page<Book> findByIsbnContains(String content, Pageable pageable) {
		return bookRepository.findByIsbnContains(content, pageable);
	}

	public Page<Book> findByAuthorContains(String content, Pageable pageable) {
		return bookRepository.findByAuthorContains(content, pageable);
	}

	public Page<Book> findByPublisherContains(String content, Pageable pageable) {
		return bookRepository.findByPublisherContains(content, pageable);
	}

	public Page<Book> findAllByOrderByHitDesc(Pageable pageable) {
		return bookRepository.findAllByOrderByHitDesc(pageable);
	}

	public Book findById(Integer[] bookid) {
		return bookRepository.findById(bookid);
	}

}
