package com.nracademy.backend.common.api;

import com.nracademy.backend.dto.response.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.nracademy.backend.controller")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body instanceof ApiResponse || body instanceof ErrorResponse) {
            return body;
        }

        if (body instanceof ResponseEntity<?> entity) {
            if (entity.getStatusCode().value() == 204 || entity.getBody() == null) {
                return body;
            }
            Object wrappedBody = wrap(entity.getBody());
            return ResponseEntity.status(entity.getStatusCode()).body(wrappedBody);
        }

        if (body instanceof Page<?> page) {
            return ApiResponse.builder().data(PageResponse.from(page, List.of())).build();
        }

        return ApiResponse.builder().data(body).build();
    }

    private Object wrap(Object body) {
        if (body instanceof ApiResponse || body instanceof ErrorResponse) {
            return body;
        }
        if (body instanceof Page<?> page) {
            return ApiResponse.builder().data(PageResponse.from(page, List.of())).build();
        }
        return ApiResponse.builder().data(body).build();
    }
}
