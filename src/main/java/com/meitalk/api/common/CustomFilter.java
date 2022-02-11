package com.meitalk.api.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Slf4j
@Component
public class CustomFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletToHttpServletRequest = (HttpServletRequest)request;
        ReadableRequestWrapper wrapper = new ReadableRequestWrapper(servletToHttpServletRequest);
        chain.doFilter(wrapper, response);
    }
}
