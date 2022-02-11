package com.meitalk.api.common.annotation;

import com.meitalk.api.model.stream.enums.RedisKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheEvict {
    
    RedisKey[] value() default {};
}
