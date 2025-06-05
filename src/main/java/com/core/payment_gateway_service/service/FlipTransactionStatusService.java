package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.repository.PaymentGatewayTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FlipTransactionStatusService {

    @Autowired
    private PaymentGatewayTransactionRepository paymentGatewayTransactionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public String checkFlipStatus(String linkId, String apiKey) {

        String url = "https://bigflip.id/big_sandbox_api/v2/pwf/" + linkId + "/payment";
        log.info("Checking Flip status at URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getBasicAuthHeader(apiKey));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                Object dataObj = body.get("data");
                if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
                    Object firstData = dataList.get(0);
                    if (firstData instanceof Map<?, ?> firstDataMap) {
                        Object statusObj = firstDataMap.get("status");
                        String status = statusObj != null ? statusObj.toString() : null;
                        log.info("Flip status for link_id {}: {}", linkId, status);
                        return status;
                    }
                }
                log.warn("Data list is empty or malformed for link_id {}", linkId);
            } else {
                log.warn("Empty response body from Flip for link_id {}", linkId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }return null;
    }

    private String getBasicAuthHeader(String apiKey) {
        String auth = apiKey + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}



