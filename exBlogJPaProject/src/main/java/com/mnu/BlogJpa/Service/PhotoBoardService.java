package com.mnu.BlogJpa.Service;

import com.mnu.BlogJpa.Entity.PhotoBoardEntity;
import com.mnu.BlogJpa.repository.PhotoBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional 
public class PhotoBoardService {

    private final PhotoBoardRepository photoBoardRepository;

    // 1. 전체 게시글 수
    public long photoCount() {
        return photoBoardRepository.count();
    }

    // 2. 글 목록 보기 (원글만)
    public Page<PhotoBoardEntity> photoListPage(Pageable pageable) {
        return photoBoardRepository.findByParentIdx(0, pageable);
    }

    // 3. 제목으로 검색
    public Page<PhotoBoardEntity> photoListSearchPage(String keyword, Pageable pageable) {
        return photoBoardRepository.findByTitleContaining(keyword, pageable);
    }

    // 4. 등록 (글/댓글 공용)
    public void photoWrite(PhotoBoardEntity entity) {
        photoBoardRepository.save(entity);
    }

    // 5. 상세 보기 (조회수 증가 포함)
    public PhotoBoardEntity photoSelect(int idx) {
        PhotoBoardEntity entity = photoBoardRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("해당 글을 찾을 수 없단다! 으헤~~"));
        
        entity.increaseHits(); 
        return entity;
    }

    // 6. 수정 처리 (DB에 맞게 필드 정리!)
    public void photoModify(PhotoBoardEntity source) {
        PhotoBoardEntity target = photoBoardRepository.findById(source.getIdx())
                .orElseThrow(() -> new RuntimeException("수정할 글이 없단다!"));
        
        // 으헤~~ DB에 있는 필드만 딱딱 바꿔주자고!
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setPhoto(source.getPhoto());
        
        // target.setCategory(...) 는 DB에 컬럼이 없어서 삭제했어! 으헤~~
    }

    // 7. 삭제 처리
    public void photoDelete(int idx) {
        photoBoardRepository.deleteById(idx);
    }

    // 8. 댓글 목록 보기
    public List<PhotoBoardEntity> photoCommentList(int parentIdx) {
        return photoBoardRepository.findByParentIdx(parentIdx, Pageable.unpaged()).getContent();
    }

    // 9. 데이터만 가져오기
    public PhotoBoardEntity photoSelectOnly(int idx) {
        return photoBoardRepository.findById(idx).orElse(null);
    }
}