package com.meitalk.api.common.annotation;

import com.meitalk.api.common.enums.Role;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtAuthentication {

    boolean required() default true;
    Role role() default Role.USER;
}
