package com.mnu.BlogJpa.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="photoboard")
@Getter 
@Setter // 으헤~~ 컨트롤러에서 바로 쓰려면 Setter가 필요해!
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoBoardEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idx;

    @Column(name="parent_idx") // DB의 parent_idx와 연결!
    private int parentIdx;

    private String title;
    private String category;
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name="writer_id") // DB의 writer_id와 연결!
    private String writer_id;

    private String photo;    // 사진 파일명
    private int hits;        // 조회수

    // DB에서 자동으로 입력하게 설정했으니 insertable/updatable은 false!
    @Column(nullable = false, updatable = false, insertable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regdate;

    // 으헤~~ 조회수 올리는 로직은 서비스에서 쓰기 좋게 남겨두자고!
    public void increaseHits() {
        this.hits++;
    }
}