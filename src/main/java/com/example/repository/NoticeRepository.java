package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

	List<Notice> findByTitleContains(String subject);

}
