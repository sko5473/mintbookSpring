package com.example.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.security.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


}
