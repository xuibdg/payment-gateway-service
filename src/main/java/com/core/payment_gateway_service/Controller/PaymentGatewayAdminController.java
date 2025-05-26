package com.core.payment_gateway_service.Controller;

import com.core.payment_gateway_service.DTO.PaymentGatewayConfigRequest;
import com.core.payment_gateway_service.DTO.PaymentGatewayConfigResponse;
import com.core.payment_gateway_service.DTO.PaymentGatewayRequest;
import com.core.payment_gateway_service.DTO.PaymentGatewayResponse;
import com.core.payment_gateway_service.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-gateway")
public class PaymentGatewayAdminController {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @PostMapping("/create")
    String createGateway(@RequestBody PaymentGatewayRequest paymentGatewayRequest){
        return paymentGatewayService.createGateway(paymentGatewayRequest);
    }

    @GetMapping("/get-all")
    List<PaymentGatewayResponse> getAllGateways(){
        return paymentGatewayService.getAllGateways();
    }

    @PutMapping("/{paymentGatewayId}/updatePG")
    String updateGateway(@PathVariable String paymentGatewayId, @RequestBody PaymentGatewayRequest paymentGatewayRequest){
        return paymentGatewayService.updateGateway(paymentGatewayId, paymentGatewayRequest);
    }

    @PostMapping("/{paymentGatewayId}/create")
    void addConfig(@PathVariable String paymentGatewayId, @RequestBody PaymentGatewayConfigRequest paymentGatewayConfigRequest){
       paymentGatewayService.addConfig(paymentGatewayId, paymentGatewayConfigRequest);
    }

    @GetMapping("/{paymentGatewayId}/get")
    List<PaymentGatewayConfigResponse> getConfigs(@PathVariable String paymentGatewayId){
        return paymentGatewayService.getConfigs(paymentGatewayId);
    }

    @PutMapping("/{pgConfigId}/updateConfig")
    String updateConfig(@PathVariable String pgConfigId, @RequestBody PaymentGatewayConfigRequest paymentGatewayConfigRequest){
        return paymentGatewayService.updateConfig(pgConfigId, paymentGatewayConfigRequest);
    }
}

