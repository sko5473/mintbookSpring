package com.example.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "point")
public class Point {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer prevPoint;
	
	private Integer changePoint;
	
	private Integer presentPoint;
	
	private LocalDateTime regDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonManagedReference(value="member-points")
	@JoinColumn(name = "member_id")
	@JsonIgnore
	private Member member;
}

