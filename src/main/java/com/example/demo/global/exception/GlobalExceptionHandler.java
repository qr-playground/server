package com.example.demo.global.exception;

import com.example.demo.global.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("잘못된 형식의 요청입니다.")
                .timestamp(LocalDateTime.now())
                .validationErrors(null)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(HttpStatus.CONFLICT.value())
                .message("중복된 ID 입니다.")
                .timestamp(LocalDateTime.now())
                .validationErrors(null)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> validationErrors = ex.getBindingResult().getFieldErrors()
                                             .stream()
                                             .map(fieldError -> fieldError.getDefaultMessage())
                                             .collect(Collectors.toList());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("잘못된 요청입니다.")
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomExceptions(BaseException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(ex.getStatus().value())  
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .validationErrors(null)
                .build();
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("예상치 못한 서버 오류 발생: {}", ex.getMessage(), ex);
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("예상치 못한 서버 오류 발생 발생")
                .timestamp(LocalDateTime.now())
                .validationErrors(null)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}