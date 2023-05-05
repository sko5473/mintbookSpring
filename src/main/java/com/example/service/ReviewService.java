package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.DTO.ReviewBookDto;
import com.example.domain.Review;
import com.example.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired ReviewRepository reviewRepository;
	
	public void save(Review review) {
		reviewRepository.save(review);
	}

	//리뷰 평점 계산
	public Double getAverageStarRatingByBookId(Integer bookId) {
		return reviewRepository.getAverageStarRatingByBookId(bookId);
	}

	public Page<Review> findAll(Pageable pageable) {
		return reviewRepository.findAll(pageable);
	}

	public Page<Review> findByWriter(String email,Pageable pageable) {
		return reviewRepository.findByWriter(email, pageable);
	}

	public Review findById(int id) {
		return reviewRepository.findById(id).get();
	}

	public void deleteById(int id) {
		reviewRepository.deleteById(id);
	}

	public Page<Review> findByWriterContains(String subject,Pageable pageable) {
		return reviewRepository.findByWriterContains(subject, pageable);
	}

	public Page<Review> findByBookBookNameContains(String subject,Pageable pageable) {
		return reviewRepository.findByBookBookNameContains(subject, pageable);
	}

	public Page<Review> findByBookIdOrderByRegDateDesc(int bookid, Pageable pageable) {
		
		return reviewRepository.findByBookIdOrderByRegDateDesc(bookid, pageable);
	}

	public Page<Review> findByBookIdOrderByStarDesc(int bookid, Pageable pageable) {
		
		return reviewRepository.findByBookIdOrderByStarDesc(bookid, pageable);
	}
	
}
