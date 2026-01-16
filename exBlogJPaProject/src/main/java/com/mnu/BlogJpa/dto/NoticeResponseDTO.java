package com.mnu.BlogJpa.dto;

import java.time.LocalDateTime;

import com.mnu.BlogJpa.Entity.NoticeEntity;

import lombok.Getter;

@Getter
public class NoticeResponseDTO {
	private int idx;
    private String title;
    private String content;
    private String writer_id;   // DB의 writer_id와 매핑될 거야
    private LocalDateTime regdate;
    private int is_top;
    private int readcnt;
    public NoticeResponseDTO(NoticeEntity entity) {
    	this.idx=entity.getIdx();
    	this.title=entity.getTitle();
    	this.content=entity.getContent();
    	this.writer_id=entity.getWriter_id();
    	this.regdate=entity.getRegdate();
    	this.is_top=entity.getIsTop();
    	this.readcnt=entity.getReadcnt();
    }
}
