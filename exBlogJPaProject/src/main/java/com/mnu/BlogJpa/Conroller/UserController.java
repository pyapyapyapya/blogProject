package com.mnu.BlogJpa.Conroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnu.BlogJpa.Entity.UserEntity;
import com.mnu.BlogJpa.Service.UserService;
import com.mnu.BlogJpa.util.UserSHA256;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor // 으헤~~ 아저씨가 좋아하는 생성자 주입!
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    // 1. 회원가입 폼 이동
    @GetMapping("/user_insert")
    public String userInsert() {
        return "login/user_insert";
    }

    // 2. 아이디 중복 체크 (AJAX)
    @ResponseBody
    @PostMapping("/login_idcheck")
    public String userIdCheck(@RequestParam("userid") String userid) {
        return String.valueOf(userService.userIdCheck(userid));
    }

    // 3. SMS 인증번호 발송 (AJAX)
    @ResponseBody
    @PostMapping("/login_sms")
    public String smsSend(@RequestParam("tel") String tel) {
        return userService.sendSMS(tel);
    }

    // 4. 회원가입 처리
    @PostMapping("/login_insert_pro")
    public String userInsertPro(UserEntity user, 
                                @RequestParam("email1") String email1, 
                                @RequestParam("email2") String email2, 
                                Model model) {
        
        // 이메일 합치기
        if (email1 != null && !email1.isEmpty() && email2 != null && !email2.isEmpty()) {
            user.setEmail(email1 + "@" + email2);
        }

        // 비밀번호 암호화 (SHA256)
        user.setPw(UserSHA256.getSHA256(user.getPw()));

        userService.userInsert(user);
        model.addAttribute("row", 1); // JPA는 성공 시 예외가 안 나면 성공으로 간주해!
        return "login/user_insert_pro";
    }

    // 5. 로그인 처리
    @PostMapping("/user_login")
    public String userLogin(UserEntity user, Model model, HttpServletRequest request) {
        log.info("로그인 시도 ID: {}", user.getId());

        // 입력받은 비밀번호를 암호화해서 DB와 비교해야 해! 으헤~~
        String encryptedPw = UserSHA256.getSHA256(user.getPw());
        UserEntity loginUser = userService.userLogin(user.getId(), encryptedPw);

        if (loginUser == null) {
            // 아이디가 없거나 비번이 틀린 경우 (JPA 검색 결과 없음)
            model.addAttribute("row", 0);
        } else {
            // 로그인 성공! 세션 저장
            HttpSession session = request.getSession();
            session.setAttribute("user", loginUser);
            session.setMaxInactiveInterval(1800);

            // 로그인 시간 업데이트
            userService.userLoginLastTimeDate(loginUser.getId());
            model.addAttribute("row", 1);
        }
        return "login/user_login_pro";
    }

    // 6. 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        log.info("으헤~~ 로그아웃 성공!");
        return "redirect:/";
    }

    // 7. 회원 정보 수정 폼
    @GetMapping("/user_modify")
    public String userModify(HttpSession session, Model model) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser == null) return "redirect:/";

        model.addAttribute("user", loginUser);
        return "login/user_modify";
    }

    // 8. 회원 정보 수정 처리
    @PostMapping("/user_modify_pro")
    public String userModifyPro(UserEntity user, 
                                @RequestParam("email1") String email1, 
                                @RequestParam("email2") String email2, 
                                HttpSession session, Model model) {
        
        UserEntity sessionUser = (UserEntity) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/";

        // 이메일 처리
        if (email1 != null && !email1.isEmpty()) {
            user.setEmail(email1 + "@" + email2);
        }

        // 비밀번호 처리
        if (user.getPw() != null && !user.getPw().isEmpty()) {
            user.setPw(UserSHA256.getSHA256(user.getPw())); // 새 비번 암호화
        } else {
            user.setPw(sessionUser.getPw()); // 입력 안 했으면 기존 비번 유지
        }

        userService.userModify(user);
        
        // 으헤~~ 수정한 정보로 세션 최신화!
        session.setAttribute("user", userService.userLogin(user.getId(), user.getPw()));
        model.addAttribute("row", 1);
        
        return "login/user_modify_pro";
    }
}