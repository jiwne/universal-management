package com.jin.java.universalresource.config;

import com.jin.java.universalresource.annotation.OpenPermission;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author：jin
 * @date：2025/4/30
 */
@Configuration
@RequiredArgsConstructor
public class OpenPermissionConfig {
    private static final Set<String> URL = new HashSet<>();


    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void init(){
        openPermissions();
    }

    public void openPermissions() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = methodEntry.getValue();
            Method method = handlerMethod.getMethod();

            // 方法或者类是否包括OpenPermission注解
            if (method.getDeclaringClass().isAnnotationPresent(OpenPermission.class) ||
                    method.isAnnotationPresent(OpenPermission.class)
            ) SetURI(methodEntry);
        }
    }

    private void SetURI(Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry) {
        RequestMappingInfo requestMappingInfo = methodEntry.getKey();
        if (requestMappingInfo != null && requestMappingInfo.getPathPatternsCondition() != null) {
            Set<PathPattern> patterns = requestMappingInfo.getPathPatternsCondition().getPatterns();
            for (PathPattern pattern : patterns) {
                String patternString = pattern.getPatternString();
                URL.add(patternString);
            }

        }
    }

    public Set<String> getOpenURL() {
        return URL;
    }

    public String[] getOpenURLArray() {
        return URL.toArray(new String[0]);
    }

}
