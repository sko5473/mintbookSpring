package com.example.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/***
 * 
 * @author LKH
 * 유저수정DTO
 */

@Data
public class MemberReviseDto {
    private String password;
    private String gender;//성별
    private String address;//주소
    private String addDetail;//상세주소
    private int age;//나이
    private String name;//이름
    private String phone;//휴대전화번호
    private LocalDate birth;//생일
}
