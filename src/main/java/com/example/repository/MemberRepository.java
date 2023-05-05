package com.example.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DTO.MemberReviseDto;
import com.example.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String username);

	Member findBySnsId(String snsId);

	//회원가입시 중복체크(가입된 이메일 수)
	int countByEmail(String email);
}
