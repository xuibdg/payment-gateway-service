package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.dto.PaymentGatewayConfigRequest;
import com.core.payment_gateway_service.dto.PaymentGatewayConfigResponse;
import com.core.payment_gateway_service.dto.PaymentGatewayRequest;
import com.core.payment_gateway_service.dto.PaymentGatewayResponse;
import com.core.payment_gateway_service.utils.BaseResponse;

import java.util.List;

public interface PaymentGatewayService {
    String createGateway(PaymentGatewayRequest paymentGatewayRequest);
    List<PaymentGatewayResponse> getAllGateways();
    String updateGateway(String paymentGatewayId, PaymentGatewayRequest paymentGatewayRequest);

    String addConfig(String paymentGatewayId, PaymentGatewayConfigRequest paymentGatewayConfigRequest);
    String updateConfig(String pgConfigId, PaymentGatewayConfigRequest paymentGatewayConfigRequest);
    List<PaymentGatewayConfigResponse> getConfigs(String paymentGatewayId);
}
