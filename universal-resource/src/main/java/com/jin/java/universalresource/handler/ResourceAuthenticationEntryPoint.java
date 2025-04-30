package com.jin.java.universalresource.handler;

import com.alibaba.fastjson2.JSONObject;
import com.jin.java.universalcommon.response.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * @author：jin
 * @date：2025/4/30
 */
@Slf4j
public class ResourceAuthenticationEntryPoint implements AuthenticationEntryPoint, Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {
    /**
     * 认证失败会调用此方法
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("认证失败");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().write(JSONObject.toJSONString(Result.failure(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED),"token无效")).getBytes());
    }

    @Override
    public void customize(ExceptionHandlingConfigurer<HttpSecurity> httpSecurityExceptionHandlingConfigurer) {
        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(this);
    }
}
