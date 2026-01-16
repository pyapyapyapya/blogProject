package com.mnu.BlogJpa.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // jakarta 말고 spring용 추천!

import com.mnu.BlogJpa.Entity.NoticeEntity;
import com.mnu.BlogJpa.repository.NoticeRepositary;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional // 으헤~~ 서비스 전체에 트랜잭션을 걸어주는 게 편해!
public class NoticeService {

    // final을 꼭 붙여줘야 @RequiredArgsConstructor가 주입해줌
    private final NoticeRepositary noticeRepository;

    // 1. 전체 글수 카운트
    public long noticeCount() {
        return noticeRepository.count();
    }

    // 2. 글 전체 목록 (페이징)
    public Page<NoticeEntity> noticeListPage(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    // 3. 전체 목록 (검색 + 페이징)
    public Page<NoticeEntity> noticeListSearchPage(String keyword, Pageable pageable) {
        return noticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    // 4. 글 등록
    public NoticeEntity noticeWrite(NoticeEntity entity) {
        return noticeRepository.save(entity); 
    }

    // 5. 상세 보기 (조회수 증가 포함)
    public NoticeEntity noticeSelect(int idx) {
        noticeRepository.updateReadCount(idx); // 조회수 증가
        return noticeRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));
    }
	 // 5-1. [신규] 조회수 증가 없이 '데이터만' 가져오는 메서드
	 @Transactional(readOnly = true)
	 public NoticeEntity noticeSelectOnly(int idx) {
	     return noticeRepository.findById(idx)
	             .orElseThrow(() -> new RuntimeException("글을 찾을 수 없단다! 으헤~~"));
	 }

    // 6. 수정 (DTO 없이 엔티티만 사용)
    public void noticeModify(NoticeEntity source) {
        //DB에서 원래 데이터를 먼저 가져온 뒤 값만 바꿔주면 끝!(더티 체킹)
        NoticeEntity target = noticeRepository.findById(source.getIdx())
                .orElseThrow(() -> new RuntimeException("수정할 글이 없습니다"));
        
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setIsTop(source.getIsTop());
        //영속성 컨텍스트 덕분에 따로 save()를 안 불러도 자동으로 UPDATE
    }

    // 7. 삭제
    public void boardDelete(int idx) {
        noticeRepository.deleteById(idx);
    }
}