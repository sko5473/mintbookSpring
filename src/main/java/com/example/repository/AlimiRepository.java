package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DTO.AlimiDto;
import com.example.domain.Alimi;

public interface AlimiRepository extends JpaRepository<Alimi, Integer> {

	//알리미 등록
	AlimiDto save(AlimiDto alimiDto);

	//내 알리미 목록 조회
	Page<Alimi> findByWriter(String email, Pageable pageable);

	//도서등록시 isbn, sendStatus가 N인 알리미 내역 조회
	List<Alimi> findByIsbnAndSendStatus(String isbn, String sendStatus);

}
