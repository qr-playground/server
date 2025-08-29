package com.example.demo.global.interceptor;

import com.example.demo.global.config.LsnContextHolder;
import com.example.demo.global.service.ReplicaConsistencyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ReplicaConsistencyInterceptor implements HandlerInterceptor {
    private final ReplicaConsistencyService replicaConsistencyService;

    public ReplicaConsistencyInterceptor(ReplicaConsistencyService replicaConsistencyService) {
        this.replicaConsistencyService = replicaConsistencyService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 읽기 계열만 체크 (GET/HEAD 기준)
        String method = request.getMethod();
        if (!("GET".equals(method) || "HEAD".equals(method))) {
            return true;
        }

        String requiredLsn = request.getHeader("X-Min-LSN");
        if (requiredLsn == null || requiredLsn.isBlank()) {
            return true;
        }

        boolean caughtUp = replicaConsistencyService.isReplicaCaughtUp(requiredLsn);
        if (caughtUp) {
            // 레플리카가 요청 LSN까지 도달 -> 슬레이브 허용
            LsnContextHolder.clear();
        } else {
            // 요청 LSN이 더 최신 -> 마스터 강제 라우팅 신호
            LsnContextHolder.setRequiredMinLsn(requiredLsn);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        LsnContextHolder.clear();
    }
}