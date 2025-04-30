package com.example.demo.global.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.error.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

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
     * @return UserDetails 구현체인 CustomUserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException(phoneNumber));

        return new CustomUserDetails(user);
    }
}