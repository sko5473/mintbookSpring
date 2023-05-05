package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.domain.Notice;
import com.example.repository.NoticeRepository;

@Service
public class NoticeService {

	@Autowired NoticeRepository noticeRepository;
	
	//공지사항 쓰기
	public Notice save(Notice params) {
		Notice notice = Notice.builder()
				.title(params.getTitle())
				.content(params.getContent())
				.writer("관리자")
				.regDate(LocalDateTime.now())
				.build();
		
		return noticeRepository.save(notice);
	}

	//공지사항 목록
	public Page<Notice> findAll(Pageable pageable) {
		return noticeRepository.findAll(pageable);
	}
	
	//공지사항 상세보기
	public Notice findById(int noticeid) {
		
		return noticeRepository.findById(noticeid).get();
	}

	//공지사항 수정
	public Notice revise(Notice notice) {
		return noticeRepository.save(notice);
	}

	//공지사항 삭제
	public void deleteById(int id) {
		noticeRepository.deleteById(id);
	}

	public List<Notice> findByTitleContains(String subject) {
		return noticeRepository.findByTitleContains(subject);
	}
}
