package com.example.DTO;

import lombok.Data;

/**
 * 
 * @author LKH 로그인용 DTO
 *
 */
@Data
public class MemberLoginRequestDto {
    private String email;
    private String password;
}
