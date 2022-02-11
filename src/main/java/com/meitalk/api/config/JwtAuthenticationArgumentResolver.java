package com.meitalk.api.config;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(JwtAuthentication.class, parameter) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String token = jwtTokenProvider.resolveToken();
        JwtAuthentication annotation = parameter.getParameterAnnotation(JwtAuthentication.class);
        if(token==null) {
            if (!annotation.required()) {
                return null;
            }
            throw new StreamException(ResponseCodeEnum.FAILED, "login");
        }
        if(jwtTokenProvider.validateToken(token, annotation.role())) return jwtTokenProvider.getClaim(token);
        else return null;
    }

    private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass, MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for (Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
}
