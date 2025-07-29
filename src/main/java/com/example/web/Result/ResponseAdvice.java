package com.example.web.Result;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断类或方法是否有@ApiResult注解
        return returnType.getDeclaringClass().isAnnotationPresent(ApiResult.class) ||
                returnType.hasMethodAnnotation(ApiResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 如果返回的对象已经是ApiResponse类型，则直接返回
        if (body instanceof ApiResponse) {
            return body;
        }
        // 对返回结果进行包装
        return ApiResponse.success(body);
    }
}