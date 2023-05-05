package com.example.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "qna")
public class Qna {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 4000)
	private String content;
	
	private LocalDateTime regDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonManagedReference(value="member-qnas")
	@JoinColumn(name = "member_id")
	@JsonIgnore
	private Member member;
	
	@JsonBackReference
	@OneToMany(mappedBy = "qna", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<QnaComment> qnacomments=new ArrayList<>();
}

