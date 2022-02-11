package com.meitalk.api.common;

import com.meitalk.api.common.annotation.RedisCacheEvict;
import com.meitalk.api.model.stream.enums.RedisKey;
import com.meitalk.api.utils.RedisCacheManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class RedisCacheAspect {

    private final RedisCacheManager redisCacheManager;

    @Around("@annotation(com.meitalk.api.common.annotation.RedisCacheEvict)")
    public Object removeCache(ProceedingJoinPoint pjp) throws Throwable {
        Object result =  pjp.proceed();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        RedisCacheEvict annotation = methodSignature.getMethod().getAnnotation(RedisCacheEvict.class);

        if (annotation.value().length == 0) {
            redisCacheManager.removeAllList();
        } else {
            RedisKey[] keys = annotation.value();
            for (RedisKey key : keys) {
                if (key.getType().equals("hash")) {
                    redisCacheManager.removeHashType(key);
                }
            }
        }
        return result;
    }
}
