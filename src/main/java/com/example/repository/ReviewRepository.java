package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	
	//책 평점
	@Query("SELECT AVG(r.star) FROM Review r WHERE r.book.id = :bookId")
	Double getAverageStarRatingByBookId(@Param("bookId") Integer bookId);
	
	//내 리뷰리스트
	Page<Review> findByWriter(String email, Pageable pageable);

	//키워드 작성자 검색
	Page<Review> findByWriterContains(String subject, Pageable pageable);

	//키워드 도서명 검색
	Page<Review> findByBookBookNameContains(String subject, Pageable pageable);

	//책 상세페이지 리뷰목록(최신순)
	Page<Review> findByBookIdOrderByRegDateDesc(int bookid, Pageable pageable);
	
	//책 상세페이지 리뷰목록(별점순)
	Page<Review> findByBookIdOrderByStarDesc(int bookid, Pageable pageable);

}
