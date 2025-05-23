package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.DTO.PaymentGatewayConfigRequest;
import com.core.payment_gateway_service.DTO.PaymentGatewayConfigResponse;
import com.core.payment_gateway_service.DTO.PaymentGatewayRequest;
import com.core.payment_gateway_service.DTO.PaymentGatewayResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentGatewayService {
    String createGateway(PaymentGatewayRequest paymentGatewayRequest);
    List<PaymentGatewayResponse> getAllGateways();
    String updateGateway(String paymentGatewayId, PaymentGatewayRequest paymentGatewayRequest);

    void addConfig(String paymentGatewayId, PaymentGatewayConfigRequest paymentGatewayConfigRequest);
    String updateConfig(String pgConfigId, PaymentGatewayConfigRequest paymentGatewayConfigRequest);
    List<PaymentGatewayConfigResponse> getConfigs(String paymentGatewayId);
}
