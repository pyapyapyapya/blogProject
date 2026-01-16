package com.mnu.BlogJpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoBoardResponseDTO {
	private int parent_idx;  // 0이면 원글, 아니면 댓글
    private String title;    
    private String content;  
    private String writer_id;
    private String photo;    // 파일명
    private String category;
    private String pass;    
}
