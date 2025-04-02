package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.UserDto;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.dto.ErrorResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 관리 API", description = "사용자 API: 등록, 조회")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "전체 사용자 조회",
        description = "등록된 모든 사용자를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공", content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 오류 발생", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping
    public ResponseEntity<List<UserDto.Response>> getAllUsers() {
        List<UserDto.Response> responses = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Operation(
        summary = "특정 사용자 조회",
        description = "주어진 ID의 사용자를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공", content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 오류 발생", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable UUID id) {
        UserDto.Response response = userService.findUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}