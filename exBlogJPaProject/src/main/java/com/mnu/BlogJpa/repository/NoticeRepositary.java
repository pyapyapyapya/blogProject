package com.mnu.BlogJpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnu.BlogJpa.Entity.NoticeEntity;
import com.mysql.cj.protocol.x.Notice;
@Repository
public interface NoticeRepositary extends JpaRepository<NoticeEntity, Integer> {
	// 1. [검색 + 페이징] 제목 또는 내용에 키워드가 포함된 글 찾기
    // 반환 타입을 Page<NoticeEntity>로 꼭 맞춰줘야 해!
    Page<NoticeEntity> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    
    // 2. [조회수 증가] 벌크 수정 쿼리
    // update NoticeEntity라고 클래스 이름을 정확히 적어주기
    @Modifying
    @Query("update NoticeEntity n set n.readcnt = n.readcnt + 1 where n.idx = :idx")
    int updateReadCount(@Param("idx") int idx);
    
    // 3. [개별 필드 검색] 엔티티 필드명(title, content)에 맞춰서
    Page<NoticeEntity> findByTitleContaining(String keyword, Pageable pageable);
    Page<NoticeEntity> findByContentContaining(String keyword, Pageable pageable);
}
