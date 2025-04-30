package com.jin.java.universalresource.handler;

import com.alibaba.fastjson2.JSONObject;
import com.jin.java.universalcommon.response.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * @author：jin
 * @date：2025/4/30
 */
@Slf4j
public class ResourceAccessDeniedHandler implements AccessDeniedHandler, Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("访问被拒绝");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().write(JSONObject.toJSONString(Result.failure(String.valueOf(HttpServletResponse.SC_FORBIDDEN),"访问被拒绝！")).getBytes());
    }

    @Override
    public void customize(ExceptionHandlingConfigurer<HttpSecurity> httpSecurityExceptionHandlingConfigurer) {
        httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(this);
    }
}
