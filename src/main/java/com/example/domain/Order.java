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
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//private Integer quantity;
	
	private Integer price;
	
	@Column(length = 255)
	private String buyer;
	
	@Column(length = 255)
	private String buyerEmail;
	
	@Column(length = 255)
	private String buyerAddress;
	
	private String status;
	
	@Column(length = 50)
	private String orderNum;
	
	private LocalDateTime orderDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonManagedReference(value="member-orders")
	@JoinColumn(name = "member_id")
	@JsonIgnore
	private Member member;
	
	@JsonBackReference
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
	private List<OrderItem> orderitems=new ArrayList<>();
}

