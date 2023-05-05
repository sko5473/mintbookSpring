package com.example.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member implements UserDetails {
 
	private static final long serialVersionUID = 1L;
    
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
	@Column(unique = true, nullable = false, length = 100)
	private String email;//이메일
    
    @Column(nullable = false, length = 255)
    private String password;//패스워드
    
    @Column(length = 10)
    private String gender;//성별
    
    @Column(length = 255)
    private String address;//주소
    
    @Column(length = 255)
    private String addDetail;//상세주소
    
    private int age;//나이
    
    @Column(length = 255)
    private String name;//이름
 
    @Column(length = 100)
    private String phone;//휴대전화번호
    
    private LocalDate birth;//생일
    
    private LocalDateTime joinDate;//가입일
    
    private int point;//보유포인트
    
    @Column(length = 10)
    private String snsType; //sns종류
    
    @Column(length = 255)
    private String snsId; //sns 고유 식별id
    
    @Column(length = 255)
    private String snsProfile; //sns 프로필이미지 url
    
    private LocalDateTime snsConnectDate; //sns연동일자
    
    //member_roles 테이블을 생성함.
    @ElementCollection(fetch = FetchType.EAGER) 
    @Builder.Default
    private List<String> roles = new ArrayList<>();//권한(ADMIN, USER)
 
    @JsonIgnore
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Review> reviews = new ArrayList<>();
	
    @JsonIgnore
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Wishlist> wishlists = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Cart> carts = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Order> orders = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Point> points = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Qna> qnas=new ArrayList<>();
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
 
    //UserDetails구현
    @Override
    public String getUsername() {
        return email;
    }
 
    @Override
    public String getPassword() {
        return password;
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return true;
    }
}
