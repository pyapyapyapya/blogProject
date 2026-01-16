package com.mnu.BlogJpa.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PhotoBoardDTO {
	private int idx;            // 게시글/댓글 고유 번호
    private int parent_idx;     // [중요] 0이면 게시글, 0이 아니면 해당 번호 글의 댓글
    
    private String title;       // 글 제목 
    private String content;     // 글 내용 또는 댓글 본문
    private String writer_id;   // 작성자 아이디 
    
    private String photo;       // 업로드된 사진 파일명 
    private String category;	//분류
    private int hits;           // 조회수
    private LocalDateTime regdate; // 작성일
    private int reply_cnt;
    private String writer_name; // 추가
    private String pass;        // 추가
}
