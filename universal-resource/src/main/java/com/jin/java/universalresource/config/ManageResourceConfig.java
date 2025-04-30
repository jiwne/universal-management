package com.jin.java.universalresource.config;

import com.jin.java.universalresource.handler.ResourceAccessDeniedHandler;
import com.jin.java.universalresource.handler.ResourceAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author：jin
 * @date：2025/4/30
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ManageResourceConfig {

    private final JwtDecoder jwtDecoder;

    private final OpenPermissionConfig openPermissionConfig;
    @Bean
    public ResourceAccessDeniedHandler resourceAccessDeniedHandler() {
        return new ResourceAccessDeniedHandler();
    }

    @Bean
    public ResourceAuthenticationEntryPoint resourceAuthenticationEntryPoint() {
        return new ResourceAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                // api server 禁用 cerf
                .csrf(AbstractHttpConfigurer::disable)
                // 启用CORS
                .cors(Customizer.withDefaults())
                // 授权配置
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers(HttpMethod.OPTIONS).permitAll()
                            .requestMatchers(openPermissionConfig.getOpenURLArray()).permitAll()
                            .anyRequest().authenticated();
                })
                // jwtDecoder
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwtConfigurer -> {
                    jwtConfigurer
                            .decoder(jwtDecoder)
                            .jwtAuthenticationConverter(jwtAuthenticationConverter());
                        })
                        // 记录安全日志
                        .accessDeniedHandler((request,response,exception) -> {
                           log.warn("Access Denied:{}",exception.getMessage());
                           resourceAccessDeniedHandler().handle(request, response, exception);
                }))
                .httpBasic(Customizer.withDefaults())
                // exception
                .exceptionHandling(exceptionHandler -> exceptionHandler
                        .authenticationEntryPoint(resourceAuthenticationEntryPoint())
                        .accessDeniedHandler(resourceAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // 可以在这里添加自定义的 JWT 认证转换器逻辑.认证信息转换
        return new JwtAuthenticationConverter();
    }
}
