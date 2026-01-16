package com.mnu.BlogJpa.Conroller;

import com.mnu.BlogJpa.Entity.PhotoBoardEntity;
import com.mnu.BlogJpa.Entity.UserEntity;
import com.mnu.BlogJpa.Service.PhotoBoardService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/photoboard")
@RequiredArgsConstructor
public class PhotoBoardController {

    private final PhotoBoardService photoBoardService;
    private static final Logger log = LoggerFactory.getLogger(PhotoBoardController.class);

    // [파일 저장 경로]
    private final String savePath = "C:\\Users\\오준현\\Documents\\workspace-spring-tools-for-eclipse-4.32.2.RELEASE\\exBlogProject\\src\\main\\resources\\static\\upload\\photoboard/";

    // 1. 글 목록
    @GetMapping("/photoboard_list")
    public String photoboardList(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "key", required = false) String key,
                                 Model model) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("idx").descending());
        Page<PhotoBoardEntity> pList;
        
        if (key == null || key.trim().isEmpty()) {
            pList = photoBoardService.photoListPage(pageable);
        } else {
            pList = photoBoardService.photoListSearchPage(key, pageable);
        }

        model.addAttribute("plist", pList.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totpage", pList.getTotalPages());
        model.addAttribute("totcount", pList.getTotalElements());
        model.addAttribute("key", key);

        return "photoboard/photoboard_list";
    }
    //2-1글 등록폼 이동
    @GetMapping("/photoboard_write")
    public String photoboardWrite(HttpSession session, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
    	
    	model.addAttribute("page",page);
    	return "photoboard/photoboard_write";
    }
    
    // 2-2. 글 등록 처리 (수정됨: writer_name 제거)
    @PostMapping("/photoboard_write_pro")
    public String photoboardWritePro(PhotoBoardEntity entity, 
                                     @RequestParam("file") MultipartFile file, 
                                     HttpSession session, Model model) {
        
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user != null) {
            entity.setWriter_id(user.getId()); 
            
        }

        if (file != null && !file.isEmpty()) {
            String saveFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            try {
                file.transferTo(new File(savePath + saveFileName));
                entity.setPhoto(saveFileName);
            } catch (IOException e) {
                log.error("파일 저장 에러: " + e.getMessage());
            }
        }

        entity.setParentIdx(0);
        photoBoardService.photoWrite(entity);
        model.addAttribute("row", 1);
        return "photoboard/photoboard_write_pro";
    }

    // 3. 상세 보기 (조회수 방어 유지)
    @GetMapping("/photoboard_view")
    public String photoboardView(@RequestParam("idx") int idx, @RequestParam("page") int page,
                                 HttpServletRequest request, HttpServletResponse response,
                                 Model model) {
        
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        String userId = (user != null) ? user.getId() : "guest";
        String viewKey = "photo_view_" + userId + "_" + idx;

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

        PhotoBoardEntity photo;
        if (!isViewed) {
            photo = photoBoardService.photoSelect(idx);
            Cookie newCookie = new Cookie(viewKey, "true");
            newCookie.setMaxAge(60 * 60 * 24);
            newCookie.setPath("/");
            response.addCookie(newCookie);
        } else {
            photo = photoBoardService.photoSelectOnly(idx);
        }

        model.addAttribute("photo", photo);
        model.addAttribute("clist", photoBoardService.photoCommentList(idx));
        model.addAttribute("page", page);
        return "photoboard/photoboard_view";
    }

    // 4. 삭제 처리 (수정됨: pass 확인 로직 제거 또는 변경 필요)
    @GetMapping("/photoboard_delete")
    public String photoboardDelete(@RequestParam("idx") int idx, Model model) {
        // 으헤~~ 지금 DB에 pass 컬럼이 없어서 비밀번호 체크를 할 수 없단다!
        // 일단은 바로 삭제하도록 처리하거나, 나중에 pass 컬럼을 추가해야 해.
        PhotoBoardEntity photo = photoBoardService.photoSelectOnly(idx);
        
        if (photo != null) {
            if (photo.getPhoto() != null) {
                new File(savePath + photo.getPhoto()).delete();
            }
            photoBoardService.photoDelete(idx);
            model.addAttribute("row", 1);
        } else {
            model.addAttribute("row", -1);
        }
        return "photoboard/photoboard_delete_pro";
    }
}