package com.jin.java.universalresource.annotation;

import java.lang.annotation.*;

/**
 * @author：jin
 * @date：2025/4/30
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenPermission {

    /**
     * 是否只能内部访问
     * @return false 开放访问， true 只能内部接口访问
     */
    boolean isInternal() default false;
}
