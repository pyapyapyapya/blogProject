package com.mnu.BlogJpa.repository;

import com.mnu.BlogJpa.Entity.UserEntity; // 으헤~~ 우리가 만든 회원 엔티티 임포트!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    
    // 1. [로그인 확인] 아이디와 비밀번호가 일치하는 회원을 찾기
    Optional<UserEntity> findByIdAndPw(String id, String pw);

    // 2. [아이디 중복 체크] 해당 아이디가 존재하는지 확인
    boolean existsById(String id);

}