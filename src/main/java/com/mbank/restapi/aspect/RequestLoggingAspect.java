package com.mbank.restapi.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingAspect.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public Object logHttpRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentHttpRequest();
        HttpServletResponse response = getCurrentHttpResponse();

        logRequest(request, joinPoint);

        Object result = joinPoint.proceed();

        logResponse(response, result);

        return result;
    }

    private void logRequest(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        if (request == null) {
            logger.warn("HttpServletRequest is null - cannot log request details.");
            return;
        }

        logger.info("===== Incoming Request =====");
        logger.info("Method  : {}", request.getMethod());
        logger.info("URI     : {}", request.getRequestURI());
        logger.info("Params  : {}", request.getQueryString());
        logger.info("Headers : {}", getHeadersAsString(request));
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            try {
                String jsonArg = objectMapper.writeValueAsString(args[i]);
                logger.info("Arg[{}]  : {}", i, jsonArg);
            } catch (Exception e) {
                logger.warn("Could not serialize argument [{}]: {}", i, e.getMessage());
            }
        }

        logger.info("============================");
    }

    private void logResponse(HttpServletResponse response, Object result) {
        logger.info("===== Response =====");
        if (response != null) {
            logger.info("Status  : {}", response.getStatus());
        }
        try {
            String jsonResult = objectMapper.writeValueAsString(result);
            logger.info("Body JSON    : {}", jsonResult);
        } catch (Exception e) {
            logger.warn("Could not serialize response body: {}", e.getMessage());
            logger.info("Body    : {}", result);
        }

        logger.info("====================");
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }

    private HttpServletResponse getCurrentHttpResponse() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getResponse() : null;
    }

    private String getHeadersAsString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            builder.append(headerName).append(": ").append(request.getHeader(headerName)).append("; ");
        });
        return builder.toString();
    }
}