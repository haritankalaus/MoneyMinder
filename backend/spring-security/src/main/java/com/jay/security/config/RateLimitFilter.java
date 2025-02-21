package com.jay.security.config;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    private static final int MAX_REQUESTS_PER_MINUTE = 30;
    
    private final LoadingCache<String, Integer> requestCountsPerIp = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIP(request);
        
        if (isMaximumRequestsPerMinuteExceeded(clientIp)) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isMaximumRequestsPerMinuteExceeded(String clientIp) {
        int requests = 0;
        try {
            requests = requestCountsPerIp.get(clientIp);
            if (requests > MAX_REQUESTS_PER_MINUTE) {
                requestCountsPerIp.put(clientIp, requests);
                return true;
            }
        } catch (ExecutionException e) {
            log.error("Error getting rate limit count", e);
        }
        requests++;
        requestCountsPerIp.put(clientIp, requests);
        return false;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/auth/");
    }
}

