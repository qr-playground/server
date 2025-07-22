package com.example.demo.domain.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.user.dto.UserDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto.Response getUser(UUID id) {
        return UserDto.Response.fromEntity(userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
    }

    /**
     * ! 🔒 Internal API — 컨트롤러에서 직접 호출하지 마세요.
     * 
     * @param phoneNumber 사용자 전화번호
     * @return 사용자 엔티티
     * @throws CustomException USER_NOT_FOUND
     */
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * ! 🔒 Internal API — 컨트롤러에서 직접 호출하지 마세요.
     * 
     * @param user 사용자 엔티티
     * @return 사용자 엔티티
     * @throws CustomException USER_NOT_FOUND
     */
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

}
