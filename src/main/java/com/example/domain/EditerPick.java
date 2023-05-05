package com.example.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "editer_pick")
public class EditerPick {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private LocalDateTime regDate;
	
	@Column(length = 4000)
	private String content;
	
	@Column(length = 200)
	private String title;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference(value="editor-book")
	@JoinColumn(name = "book_id")
	private Book book;
}
