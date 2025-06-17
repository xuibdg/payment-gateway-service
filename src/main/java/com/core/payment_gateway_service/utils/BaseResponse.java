package com.core.payment_gateway_service.utils;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class BaseResponse<T> {

    private HttpStatus httpStatus;

    private Integer status;

    private String message;

    private T data;

    public static BaseResponse buildSuccessResponse(Object data) {
        return BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status(0)
                .message("ok")
                .data(data)
                .build();
    }
}
