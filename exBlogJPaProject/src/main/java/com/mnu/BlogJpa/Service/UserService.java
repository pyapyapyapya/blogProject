package com.mnu.BlogJpa.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mnu.BlogJpa.Entity.UserEntity;
import com.mnu.BlogJpa.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.model.Message; 
@Service
@RequiredArgsConstructor
@Transactional 
public class UserService {

    // Coolsms 설정값 (application.properties에서 가져옴)
    @Value("${coolsms.apikey}")
    private String apikey;
    
    @Value("${coolsms.apisecret}")
    private String apisecret;
    
    @Value("${coolsms.formnumber}")
    private String formnumber;

    private final UserRepository userRepository;

    // 1. 아이디 중복 검사
    // 존재하면 1, 없으면 0 반환 (기존 컨트롤러 로직 유지용)
    public int userIdCheck(String userid) {
        return userRepository.existsById(userid) ? 1 : 0;
    }

    // 2. 회원가입 처리
    public void userInsert(UserEntity entity) {
        userRepository.save(entity);
    }

    // 3. 로그인 처리 (아이디와 비밀번호로 찾기)
    public UserEntity userLogin(String id, String pw) {
        return userRepository.findByIdAndPw(id, pw)
                .orElse(null); // 로그인 실패 시 null 반환
    }

    // 4. 최근 로그인 시간 업데이트 (JPA 더티 체킹)
    public void userLoginLastTimeDate(String id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다!"));
       
        user.setLast_time(null); // 쿼리를 발생시키기 위한 트리거
    }

    // 5. 회원정보 수정
    public void userModify(UserEntity source) {
        UserEntity target = userRepository.findById(source.getId())
                .orElseThrow(() -> new RuntimeException("수정할 회원이 없단다!"));
        
        target.setPw(source.getPw());
        target.setName(source.getName());
        target.setTel(source.getTel());
        target.setEmail(source.getEmail());
        // 으헤~~ save() 안 해도 트랜잭션 끝나면 UPDATE 자동 실행!
    }

    // --- SMS 인증 관련 로직 (기존 유지) ---

    public String sendSMS(String phoneNumber) {
        String tempNum = tempRandomNumber(); 
        try {
            // 으헤~~ SDK 버전이나 URL 설정은 기존 코드를 참고했어!
            DefaultMessageService messageService = 
                NurigoApp.INSTANCE.initialize(apikey, apisecret, "https://api.coolsms.co.kr");
                
            Message message = new Message();
            message.setFrom(formnumber); 
            message.setTo(phoneNumber);
            message.setText(" 인증번호 : " + tempNum);
            
            messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println("전송 성공: " + tempNum);
        } catch (Exception e) {
            System.out.println("전송 실패 사유: " + e.getMessage());
        }
        return tempNum;
    }

    private String tempRandomNumber() {
        return String.format("%04d", (int)(Math.random() * 10000));
    }
}