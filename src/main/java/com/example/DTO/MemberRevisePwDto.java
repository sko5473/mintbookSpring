package com.example.DTO;

import lombok.Data;
/***
 * 
 * @author LKH
 * 패스워드 변경시 DTO
 */
@Data
public class MemberRevisePwDto {

	//현재 암호
	private String prePassword;
	
	//바뀔 암호
	private String changePassword;
}
