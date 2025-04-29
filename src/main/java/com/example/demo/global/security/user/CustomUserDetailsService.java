package com.example.demo.global.security.user;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security 인증에 사용되는 UserDetailsService 구현체
 * 사용자 정보를 DB에서 조회하여 UserDetails 객체로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 전화번호를 기반으로 사용자 정보를 조회하여 UserDetails 객체로 변환
     * 
     * @param phoneNumber 사용자 전화번호 (username 역할)
     * @return UserDetails 구현체인 PrincipalDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("해당 전화번호로 가입된 사용자를 찾을 수 없습니다: " + phoneNumber));

        return new CustomUserDetails(user);
    }
}