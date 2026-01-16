package com.mnu.BlogJpa.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="member") // DB 사진에 있던 테이블 이름이야!
@Getter
@Setter // 으헤~~ DTO 없이 직접 입력을 받으려면 Setter가 꼭 필요해
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id // 회원 ID를 기본키(PK)로 사용한단다
    @Column(length = 50)
    private String id;

    @Column(nullable = false)
    private String pw;

    @Column(nullable = false, length = 30)
    private String name;

    private String tel;
    private String email;

    @Column(columnDefinition = "int default 0") // 0: 일반, 1: 어드민
    private int role;

    // 최근 로그인 날짜: 수업 스타일대로 String과 DB 기본값을 섞었어!
    @Column(name="last_time", insertable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private String last_time;

    // 으헤~~ 회원 가입 시 사용할 빌더 생성자야!
    @Builder
    public UserEntity(String id, String pw, String name, String tel, String email, int role) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.role = role;
    }
}