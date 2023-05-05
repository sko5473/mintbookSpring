package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.DTO.AlimiDto;
import com.example.domain.Alimi;
import com.example.repository.AlimiRepository;

@Service
public class AlimiService {

	@Autowired AlimiRepository alimiRepository;
	
	//알리미 등록
	public Alimi save(Alimi alimi) {
		
		return alimiRepository.save(alimi);
	}

	//내 알리미 목록 조회
	public Page<Alimi> findByWriter(String email, Pageable pageable) {
		
		return alimiRepository.findByWriter(email, pageable);
	}

	//내 알리미 삭제
	public void deleteById(Integer alimiid) {
		alimiRepository.deleteById(alimiid);
	}

	public List<Alimi> findByIsbnAndSendStatus(String isbn, String sendStatus) {
		
		return alimiRepository.findByIsbnAndSendStatus(isbn, sendStatus);
	}
}
