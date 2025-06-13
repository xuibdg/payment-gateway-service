package com.core.payment_gateway_service.service.impl;

import com.core.payment_gateway_service.config.FlipConfiguration;
import com.core.payment_gateway_service.repository.PaymentGatewayRepository;
import com.core.payment_gateway_service.repository.PaymentGatewayTransactionRepository;
import com.core.payment_gateway_service.service.BillPaymentService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class BillPaymentServiceImpl implements BillPaymentService {

    @Value("${flip.api-key-get-bill-payment-link}")
    private String billPaymentLinkUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FlipConfiguration flipConfig;

    @Override
    public ResponseEntity<?> getBillPaymentLink(String linkId, String  billPaymentId, String paymentGatewayId, String pgTransactionId) {

        String url = billPaymentLinkUrl.replace("{linkId}", linkId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", flipConfig.getToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            String.class
        );

        // Parse and map the response
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> original = mapper.readValue(response.getBody(), java.util.Map.class);
            java.util.Map<String, Object> mapped = new java.util.HashMap<>();

            mapped.put("bill_link_id", original.get("link_id"));
            mapped.put("bill_link", original.get("link_url"));
            mapped.put("bill_title", original.get("title"));
            mapped.put("amount", original.get("amount"));
            mapped.put("status", "SUCCESSFUL"); // SUCCESSFUL, CANCELLED, FAILED

            // Sender info
            java.util.Map<String, Object> customer = (java.util.Map<String, Object>) original.get("customer");
            if (customer != null) {
                mapped.put("sender_name", customer.get("name"));
                mapped.put("sender_email", customer.get("email"));
            }

            // Payment method info
            java.util.Map<String, Object> paymentMethod = (java.util.Map<String, Object>) original.get("payment_method");
            if (paymentMethod != null) {
                mapped.put("sender_bank", paymentMethod.get("sender_bank"));
                mapped.put("sender_bank_type", paymentMethod.get("sender_bank_type"));
            }

            mapped.put("id", billPaymentId);

            mapped.put("external_transaction_id", original.get("link_id"));

            mapped.put("payment_gateway_id", paymentGatewayId);
            mapped.put("pg_transaction_id", pgTransactionId);
            return ResponseEntity.status(response.getStatusCode()).body(mapped);
        } catch (Exception e) {
            log.error("Failed to map bill payment link response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to map response");
        }
    }
}
