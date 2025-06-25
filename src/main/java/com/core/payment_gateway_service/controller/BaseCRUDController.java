package com.core.payment_gateway_service.controller;

import com.core.payment_gateway_service.utils.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public abstract class BaseCRUDController {
    public static BaseResponse buildSuccessResponse(Object data) {
        return BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status(0)
                .message("ok")
                .data(data)
                .build();
    }

    public static BaseResponse buildErrorResponse(String message) {
        return BaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(400)
                .message(message)
                .data(null)
                .build();
    }

}
