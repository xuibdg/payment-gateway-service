package com.core.payment_gateway_service.service;

import org.springframework.http.ResponseEntity;

public interface BillPaymentService {
    ResponseEntity<?> getBillPaymentLink(String linkId, String billPaymentId, String paymentGatewayId, String pgTransactionId);
}
