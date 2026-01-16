package com.mnu.BlogJpa.Conroller;

import com.mnu.BlogJpa.Service.NoticeService;
import com.mnu.BlogJpa.Service.PhotoBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor 
public class MainController {

    private final NoticeService noticeService;
    private final PhotoBoardService photoBoardService;

    @GetMapping("/")
    public String mainIndex(Model model) {
        // 1. 공지사항 최신글 5개 설정 (0페이지, 5개씩, idx 기준 내림차순)
        Pageable noticePageable = PageRequest.of(0, 5, Sort.by("idx").descending());
        
        // 2. 사진 게시판 최신글 5개 설정 (0페이지, 5개씩, idx 기준 내림차순)
        Pageable photoPageable = PageRequest.of(0, 5, Sort.by("idx").descending());

        // 3. 서비스 호출 및 데이터 전달
        model.addAttribute("latestNotices", noticeService.noticeListPage(noticePageable).getContent());
        model.addAttribute("latestPhotos", photoBoardService.photoListPage(photoPageable).getContent());

        return "index"; // index.jsp 또는 index.html로 이동!
    }
}