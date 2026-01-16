package com.mnu.BlogJpa.repository;

import com.mnu.BlogJpa.Entity.PhotoBoardEntity; // 으헤~~ 우리가 만든 엔티티를 임포트해야 해!
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoBoardRepository extends JpaRepository<PhotoBoardEntity, Integer> {
    
    // 1. [원글/댓글 구분 검색] 
    // parentIdx가 0인 것만 찾으면 원글 목록
    Page<PhotoBoardEntity> findByParentIdx(int parentIdx, Pageable pageable);

    // 2. [제목 검색] 
    Page<PhotoBoardEntity> findByTitleContaining(String keyword, Pageable pageable);

    //등록(save), 삭제(deleteById), 상세보기(findById)는기본
}