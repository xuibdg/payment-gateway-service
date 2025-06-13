package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.dto.PaymentGatewayConfigRequest;
import com.core.payment_gateway_service.dto.PaymentGatewayConfigResponse;
import com.core.payment_gateway_service.dto.PaymentGatewayRequest;
import com.core.payment_gateway_service.dto.PaymentGatewayResponse;
import com.core.payment_gateway_service.entity.PaymentGateway;
import com.core.payment_gateway_service.entity.PaymentGatewayConfig;
import com.core.payment_gateway_service.repository.PaymentGatewayConfigRepository;
import com.core.payment_gateway_service.repository.PaymentGatewayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class PaymentGatewayServiceImpl implements PaymentGatewayService{

    @Autowired
    private PaymentGatewayRepository paymentGatewayRepository;

    @Autowired
    private PaymentGatewayConfigRepository paymentGatewayConfigRepository;


    @Override
    public String createGateway(PaymentGatewayRequest paymentGatewayRequest) {
        PaymentGateway paymentGateway = PaymentGateway.builder()
                .gatewayName(paymentGatewayRequest.getGatewayName())
                .gatewayCode(paymentGatewayRequest.getGatewayCode())
                .description(paymentGatewayRequest.getDescription())
                .isActive(paymentGatewayRequest.getIsActive())
                .createdAt(Timestamp.from(Instant.now()))
                .updatedAt(Timestamp.from(Instant.now()))
                .build();
        paymentGatewayRepository.saveAndFlush(paymentGateway);
        return "Success Create Gateway";
    }

    @Override
    public List<PaymentGatewayResponse> getAllGateways() {
        return paymentGatewayRepository.findAll().stream().map(data ->{
            return PaymentGatewayResponse.builder()
                    .paymentGatewayId(data.getPaymentGatewayId())
                    .gatewayName(data.getGatewayName())
                    .gatewayCode(data.getGatewayCode())
                    .isActive(data.getIsActive())
                    .description(data.getDescription())
                    .createdAt(data.getCreatedAt())
                    .updatedAt(data.getUpdatedAt())
                    .build();

        }).collect(Collectors.toList());
    }

    @Override
    public String updateGateway(String paymentGatewayId, PaymentGatewayRequest paymentGatewayRequest) {
        paymentGatewayRepository.findById(paymentGatewayId).map(data -> {
            data.setGatewayName(paymentGatewayRequest.getGatewayName());
            data.setGatewayCode(paymentGatewayRequest.getGatewayCode());
            data.setIsActive(paymentGatewayRequest.getIsActive());
            data.setDescription(paymentGatewayRequest.getDescription());
            data.setUpdatedAt(Timestamp.from(Instant.now()));
            paymentGatewayRepository.saveAndFlush(data);
            return (data);
        }).orElseThrow(()-> new RuntimeException("Id not Found"));

        return "Success update Payment Gateway";
    }

    @Override
    public void addConfig(String paymentGatewayId, PaymentGatewayConfigRequest paymentGatewayConfigRequest) {
        PaymentGateway paymentGateway = paymentGatewayRepository.findById(paymentGatewayId).orElseThrow(()-> new EntityNotFoundException("Payment Gateway ID Not Found"));

        PaymentGatewayConfig paymentGatewayConfig = paymentGatewayConfigRepository.findByPaymentGateway(paymentGateway).stream().filter(c ->c.getConfigName().equalsIgnoreCase(paymentGatewayConfigRequest.getConfigName())).findFirst().orElse(new PaymentGatewayConfig());

        paymentGatewayConfig.setPaymentGateway(paymentGateway);
        paymentGatewayConfig.setConfigName(paymentGatewayConfigRequest.getConfigName());
        paymentGatewayConfig.setConfigValue(paymentGatewayConfigRequest.getConfigValue());
        paymentGatewayConfig.setIsActive(paymentGatewayConfigRequest.getIsActive());
        paymentGatewayConfig.setIsSensitive(paymentGatewayConfigRequest.getIsSensitive());
        paymentGatewayConfig.setDescription(paymentGatewayConfigRequest.getDescription());
        paymentGatewayConfig.setUpdatedAt(Timestamp.from(Instant.now()));
        paymentGatewayConfigRepository.saveAndFlush(paymentGatewayConfig);
    }

    @Override
    public String updateConfig(String pgConfigId, PaymentGatewayConfigRequest paymentGatewayConfigRequest) {
        paymentGatewayConfigRepository.findById(pgConfigId).map(data -> {
            data.setConfigName(paymentGatewayConfigRequest.getConfigName());
            data.setConfigValue(paymentGatewayConfigRequest.getConfigValue());
            data.setIsActive(paymentGatewayConfigRequest.getIsActive());
            data.setIsSensitive(paymentGatewayConfigRequest.getIsSensitive());
            data.setDescription(paymentGatewayConfigRequest.getDescription());
            data.setUpdatedAt(Timestamp.from(Instant.now()));
            paymentGatewayConfigRepository.saveAndFlush(data);
            return (data);
        }).orElseThrow(()-> new EntityNotFoundException("Id not Found"));
        return "Success update Config";
    }

    @Override
    public List<PaymentGatewayConfigResponse> getConfigs(String paymentGatewayId) {
       PaymentGateway paymentGateway = paymentGatewayRepository.findById(paymentGatewayId).orElseThrow(()-> new EntityNotFoundException("Gateway Not Found"));
       return paymentGatewayConfigRepository.findByPaymentGateway(paymentGateway).stream().map(data ->{
           return PaymentGatewayConfigResponse.builder()
                   .pgConfigId(data.getPgConfigId())
                   .configName(data.getConfigName())
                   .configValue(data.getConfigValue())
                   .isSensitive(data.getIsSensitive())
                   .description(data.getDescription())
                   .isActive(data.getIsActive())
                   .createdAt(data.getCreatedAt())
                   .updatedAt(data.getUpdatedAt())
                   .build();
       }).collect(Collectors.toList());
    }
}
