package com.mnu.BlogJpa.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="notice") // DB 테이블 이름 일치 확인!
@Getter
@Setter // 으헤~~ DTO 없이 쓰려면 컨트롤러에서 값을 채워줄 Setter가 필수란다!
@NoArgsConstructor
@AllArgsConstructor // Builder와 함께 쓰기 위해 추가하는 게 좋아
@Builder
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idx;

    private String title;
    private String content;

    @Column(name="writer_id")
    private String writer_id; 

    @Column(nullable = false, updatable = false, insertable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regdate; 

    @Column(name="is_top") // 공지 상단 고정 여부
    private int isTop;

    private int readcnt;

    
    public void increaseReadCnt() {
        this.readcnt++;
    }
}