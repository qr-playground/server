package com.example.demo.domain.user.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.user.dto.UserDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto.Response findUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없음. ID: " + id));
        return UserDto.Response.fromEntity(user);
    }

    public List<UserDto.Response> findAllUsers() {
        List<User> users = userRepository.findAll();
        return UserDto.Response.fromEntityList(users);
    }
}