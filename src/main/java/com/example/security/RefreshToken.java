package com.example.security;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * 
 * @author LKH
 * RTK REDIS ENTITY
 */
@RedisHash("refreshToken")
@Builder @AllArgsConstructor @NoArgsConstructor
@Data
public class RefreshToken {
	
	@Id
	private String id;
	
	private String refresh_token;
	
	//만료기한
	@TimeToLive(unit = TimeUnit.SECONDS)
	private Integer expiration;
	
}
