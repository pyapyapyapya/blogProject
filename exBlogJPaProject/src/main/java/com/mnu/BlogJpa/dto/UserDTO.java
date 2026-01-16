package com.mnu.BlogJpa.dto;

import lombok.Data;

@Data
public class UserDTO {
	private String id;  // 회원 ID
    private String pw;  // 비밀번호
    private String name;    // 이름
    private String tel;     // 전화번호
    private String email;    // 이메일 (이메일1 + @ + 이메일2 합친 것)
    private int role;       // 0: 일반, 1: 어드민
    private String last_time;//최근 로그인 날짜
}
