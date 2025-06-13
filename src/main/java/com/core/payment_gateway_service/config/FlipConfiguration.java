package com.core.payment_gateway_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "flip")
@Data
public class FlipConfiguration {
    private String token;
    private String bankInquiryUrl;
    private String callbackValidasiToken;
    private String disbursmentUrl;
    private String apiKeyBillPaymentUrl;
    private String apiKeyGetBillPaymentLink;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
