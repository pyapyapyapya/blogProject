package com.mnu.BlogJpa.dto;

import com.mnu.BlogJpa.Entity.NoticeEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class NoticeRequsetDTO {
	private String title;
    private String content;
    private String writer_id;
    
    public NoticeEntity toEntity() {
    	return NoticeEntity.builder()
    			.title(title)
    			.content(content)
    			.writer_id(writer_id)
    			.build();
    }
}
