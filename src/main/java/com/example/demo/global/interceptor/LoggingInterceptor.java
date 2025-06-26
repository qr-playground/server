package com.example.demo.global.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    // 컨트롤러 실행 전
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            log.info("[REQUEST] URI: {}, Method: {}, Controller: {}",
                    request.getRequestURI(),
                    request.getMethod(),
                    handlerMethod.getBeanType().getSimpleName());
        } else {
            log.info("[REQUEST] URI: {}, Method: {}", request.getRequestURI(), request.getMethod());
        }

        // 요청 시작 시간 기록
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    // 컨트롤러 실행 후, JSON 변환 내보내기 전
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 컨트롤러 실행 후, 뷰 렌더링 전
        // API 서버에서는 보통 사용할 일이 많지 않습니다.
    }

    // 컨트롤러 실행 후, JOSN 변환 내보낸 후, DispatcherServlet 종료 전
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 예외가 발생한 경우와 정상 처리된 경우를 분리하여 로깅합니다.
        if (ex != null) {
            // 예외 발생 시: 에러 로그를 남깁니다.
            log.error("[EXCEPTION] URI: {}, Status: {}, Message: {}",
                    request.getRequestURI(),
                    response.getStatus(),
                    ex.getMessage(),
                    ex); // 예외 객체를 마지막 인자로 넘겨 스택 트레이스를 출력합니다.
        } else {
            // 정상 처리 시: 정보 로그를 남깁니다.
            log.info("[RESPONSE] URI: {}, Status: {}, ExecutionTime: {}ms",
                    request.getRequestURI(),
                    response.getStatus(),
                    executionTime);
        }
    }
}
