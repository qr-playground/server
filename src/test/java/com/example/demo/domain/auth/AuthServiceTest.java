package com.example.demo.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.domain.auth.dto.AuthDto;
import com.example.demo.domain.auth.repository.RefreshTokenRepository;
import com.example.demo.domain.auth.service.AuthService;
import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.jwt.JwtProperties;
import com.example.demo.global.security.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void signup_success() {
        // given
        AuthDto.Signup signupDto = new AuthDto.Signup("01012345678", "password");
        String encodedPassword = "encoded_password";
        User savedUser = new User("01012345678", encodedPassword, Role.USER);

        // 핸드폰 번호로 사용자 없음
        when(userService.getUserByPhoneNumber(signupDto.getPhoneNumber()))
                .thenReturn(Optional.empty());
        // 패스워드 인코딩
        when(passwordEncoder.encode(signupDto.getPassword()))
                .thenReturn(encodedPassword);
        // 새 사용자 저장
        when(userService.createUser(any(User.class)))
                .thenReturn(savedUser);

        // when
        AuthDto.Response response = authService.signup(signupDto);

        // then
        assertThat(response.getUserInfo().getPhoneNumber()).isEqualTo("01012345678");
        assertThat(response.getUserInfo().getRole()).isEqualTo(Role.USER);
    }

}
