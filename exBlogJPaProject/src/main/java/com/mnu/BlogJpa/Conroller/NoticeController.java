package com.mnu.BlogJpa.Conroller;

import com.mnu.BlogJpa.Entity.NoticeEntity;
import com.mnu.BlogJpa.Entity.UserEntity;
import com.mnu.BlogJpa.Service.NoticeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor 
public class NoticeController {

    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);
    private final NoticeService noticeService;

    // 1. 공지사항 목록 (JPA 페이징 적용)
    @RequestMapping("/notice/notice_board")
    public String noticeList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "key", required = false) String key,
            Model model) {

        log.info("목록 호출 - 페이지: {}, 검색키워드: {}", page, key);

        
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("idx").descending());
        
        Page<NoticeEntity> nList;
        if (key == null || key.isEmpty()) {
            nList = noticeService.noticeListPage(pageable);
        } else {
           
            nList = noticeService.noticeListSearchPage(key, pageable);
        }

        
        model.addAttribute("nlist", nList.getContent()); // 실제 리스트 데이터
        model.addAttribute("page", page);
        model.addAttribute("totpage", nList.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totcount", nList.getTotalElements()); // 전체 글 수
        model.addAttribute("search", search);
        model.addAttribute("key", key);

        return "notice/notice_board";
    }

    // 2. 글쓰기 폼 (어드민 체크)
    @GetMapping("/notice/notice_write")
    public String noticeWrite(HttpSession session, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");

        // 으헤~~ 롤값이 1(어드민)인지 체크!
        if (user == null || user.getRole() != 1) {
            return "redirect:/notice/notice_board?page=" + page;
        }

        model.addAttribute("page", page);
        return "notice/notice_write";
    }

    // 3. 글 등록 처리
    @PostMapping("/notice/notice_write")
    public String noticeWritePro(HttpSession session, NoticeEntity notice, @RequestParam(value = "page", defaultValue = "1") int page) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        if (user == null || user.getRole() != 1) return "redirect:/notice/notice_board";

        notice.setWriter_id(user.getId());
        noticeService.noticeWrite(notice);
        
        return "redirect:/notice/notice_board?page=" + page;
    }

    // 4. 상세 보기 (쿠키 기반 조회수 방어 유지)
    @GetMapping("/notice/notice_view")
    public String noticeView(@RequestParam("idx") int idx, @RequestParam("page") int page,
                             HttpServletRequest request, HttpServletResponse response,
                             HttpSession session, Model model) {

        UserEntity user = (UserEntity) session.getAttribute("user");
        String userId = (user != null) ? user.getId() : "guest";
        String viewKey = "viewed_notice_" + userId + "_" + idx;

        Cookie[] cookies = request.getCookies();
        boolean isViewed = false;

        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(viewKey)) {
                    isViewed = true;
                    break;
                }
            }
        }

        NoticeEntity notice;
        if (!isViewed) {
            notice = noticeService.noticeSelect(idx); // 조회수 증가 포함
            Cookie newCookie = new Cookie(viewKey, "true");
            newCookie.setMaxAge(60 * 60 * 24); // 24시간
            newCookie.setPath("/");
            response.addCookie(newCookie);
        } else {
            notice = noticeService.noticeSelectOnly(idx); // 데이터만 조회
        }

        model.addAttribute("notice", notice);
        model.addAttribute("page", page);
        return "notice/notice_view";
    }

    // 5. 수정 폼
    @GetMapping("/notice/notice_modify")
    public String noticeModify(@RequestParam("idx") int idx, @RequestParam("page") int page, Model model) {
        model.addAttribute("notice", noticeService.noticeSelectOnly(idx));
        model.addAttribute("page", page);
        return "notice/notice_modify";
    }

    // 6. 수정 처리
    @PostMapping("/notice/notice_modify")
    public String noticeModifyPro(NoticeEntity notice, @RequestParam("page") int page) {
        noticeService.noticeModify(notice);
        return "redirect:/notice/notice_board?page=" + page;
    }
}