package com.core.payment_gateway_service.repository;


import com.core.payment_gateway_service.entity.PaymentGateway;
import com.core.payment_gateway_service.entity.PaymentGatewayConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PaymentGatewayConfigRepository extends JpaRepository<PaymentGatewayConfig, String> {
    List<PaymentGatewayConfig> findByPaymentGateway(PaymentGateway paymentGateway);

    //Optional<PaymentGatewayConfig> findByPaymentGateway_GatewayCodeAndConfigName(String gatewayCode, String configName);
    PaymentGatewayConfig findByPaymentGateway_GatewayCodeAndConfigName(String gatewayCode, String configName);

}

