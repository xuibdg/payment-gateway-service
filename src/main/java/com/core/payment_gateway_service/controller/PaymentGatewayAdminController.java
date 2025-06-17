package com.core.payment_gateway_service.controller;

import com.core.payment_gateway_service.dto.PaymentGatewayConfigRequest;
import com.core.payment_gateway_service.dto.PaymentGatewayConfigResponse;
import com.core.payment_gateway_service.dto.PaymentGatewayRequest;
import com.core.payment_gateway_service.dto.PaymentGatewayResponse;
import com.core.payment_gateway_service.service.PaymentGatewayService;
import com.core.payment_gateway_service.utils.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.core.payment_gateway_service.utils.BaseResponse.buildSuccessResponse;


@RestController
@RequestMapping("/api/payment-gateway")
public class PaymentGatewayAdminController {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @PostMapping("/create")
    public BaseResponse createGateway(@RequestBody PaymentGatewayRequest paymentGatewayRequest){
        return buildSuccessResponse(paymentGatewayService.createGateway(paymentGatewayRequest));
    }

    @GetMapping("/get-all")
    public BaseResponse <List<PaymentGatewayResponse>> getAllGateways(){
        return buildSuccessResponse(paymentGatewayService.getAllGateways());
    }

    @PutMapping("/{paymentGatewayId}/updatePG")
    public BaseResponse updateGateway(@PathVariable String paymentGatewayId, @RequestBody PaymentGatewayRequest paymentGatewayRequest){
        return buildSuccessResponse(paymentGatewayService.updateGateway(paymentGatewayId, paymentGatewayRequest));
    }

    @PostMapping("/{paymentGatewayId}/create")
    public BaseResponse addConfig(@PathVariable String paymentGatewayId, @RequestBody PaymentGatewayConfigRequest paymentGatewayConfigRequest){
       return buildSuccessResponse(paymentGatewayService.addConfig(paymentGatewayId, paymentGatewayConfigRequest));
    }

    @GetMapping("/{paymentGatewayId}/get")
    public BaseResponse <List<PaymentGatewayConfigResponse>> getConfigs(@PathVariable String paymentGatewayId){
        return buildSuccessResponse(paymentGatewayService.getConfigs(paymentGatewayId));
    }

    @PutMapping("/{pgConfigId}/updateConfig")
    public BaseResponse updateConfig(@PathVariable String pgConfigId, @RequestBody PaymentGatewayConfigRequest paymentGatewayConfigRequest){
        return buildSuccessResponse(paymentGatewayService.updateConfig(pgConfigId, paymentGatewayConfigRequest));
    }
}

