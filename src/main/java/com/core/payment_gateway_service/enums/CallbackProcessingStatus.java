package com.core.payment_gateway_service.enums;

public enum CallbackProcessingStatus {
    RECEIVED, PROCESSING, VERIFICATION_FAILED, PROCESSED_SUCCESS, PROCESSED_ERROR,
    IGNORED_DUPLICATE, IGNORED_IRRELEVANT
}
