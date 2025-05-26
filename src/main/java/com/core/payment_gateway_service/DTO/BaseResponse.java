package com.core.payment_gateway_service.DTO;


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

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .httpStatus(HttpStatus.OK)
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> created(T data) {
        return BaseResponse.<T>builder()
                .httpStatus(HttpStatus.CREATED)
                .status(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(data)
                .build();
    }

    public static BaseResponse<?> error(HttpStatus httpStatus, String message) {
        return BaseResponse.builder()
                .httpStatus(httpStatus)
                .status(httpStatus.value())
                .message(message)
                .build();
    }


}
