package com.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Notice;
import com.example.service.NoticeService;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

	@Autowired NoticeService noticeService;
	
	//공지사항 등록
	@PostMapping("/write")
	public ResponseEntity noticeWrite(@RequestBody Notice params) {
		
		Notice writeResult = noticeService.save(params);
		
		return new ResponseEntity<>(writeResult, HttpStatus.OK);
	}
	
	//공지사항 리스트
	@GetMapping("/noticelist")
	public ResponseEntity<Page<Notice>> noticeList(@RequestParam("page") int page, @RequestParam("size") int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Notice> noticeList = noticeService.findAll(pageable);
		
		return new ResponseEntity<Page<Notice>>(noticeList, HttpStatus.OK);
	}
	
	//공지사항 상세보기
	@GetMapping("/noticeone")
	public ResponseEntity noticeOne(@RequestParam("id") int noticeid) {
		
		Notice noticeOne = noticeService.findById(noticeid);
		
		return new ResponseEntity<>(noticeOne, HttpStatus.OK);
	}
	
	//공지사항 수정
	@PutMapping("/revise/{id}")
	public ResponseEntity noticeRevise(@PathVariable("id") int id, @RequestBody Notice params) {
		
		Notice notice = noticeService.findById(id);
		
		notice.setTitle(params.getTitle());
		notice.setContent(params.getContent());
		notice.setReviseDate(LocalDateTime.now());
		
		Notice result = noticeService.revise(notice);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	//공지사항 삭제
	@DeleteMapping("/delete/{id}")
	public ResponseEntity noticeDelete(@PathVariable("id")int id) {
		
		noticeService.deleteById(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//공지사항 검색
	@GetMapping("/search")
	public ResponseEntity noticeSearch(@RequestParam("subject") String subject) {
		
		List<Notice> result = noticeService.findByTitleContains(subject);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
