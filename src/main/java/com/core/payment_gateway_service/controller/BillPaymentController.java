package com.core.payment_gateway_service.controller;

import com.core.payment_gateway_service.config.FlipConfiguration;
import com.core.payment_gateway_service.dto.*;
import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.repository.PaymentGatewayTransactionRepository;
import com.core.payment_gateway_service.service.BillPaymentService;
import com.core.payment_gateway_service.service.PaymentFlip;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bill-payment")
public class BillPaymentController {

    private final PaymentFlip paymentFlip;

    private final BillPaymentService billPaymentService;

    private final FlipConfiguration flipConfig;

    private final PaymentGatewayTransactionRepository paymentGatewayTransactionRepository;

    @PostMapping(value = "/process-bill-payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlipResponse> processBillPayment(@RequestBody BillPaymentRequest request) {
        try {
            FlipResponse response = paymentFlip.billProses(request);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new FlipResponse("ERROR", "Response from Flip is null"));
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FlipResponse("ERROR", e.getMessage()));
        }
    }

    @PostMapping("/callback")
    public String handleCallback(@RequestParam("data") String dataJson,
                                 @RequestParam("token") String token) {
        try {
            paymentFlip.callback(dataJson, token);
            return "Callback received";
        } catch (SecurityException se) {
            log.warn("Unauthorized callback received: {}", se.getMessage());
            return "Unauthorized";
        } catch (Exception e) {
            log.error("Callback error: {}", e.getMessage(), e);
            return "Error";
        }
    }

    @PostMapping("/transbackV2")
    public ResponseEntity<?> handleBillPaymentV2(HttpServletRequest request) throws IOException {// Ambil data bill payment link dari service
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            // === REQUEST PERTAMA: Buat BillPayment + Transaction + Callback ===
            BillPaymentRequest billPaymentRequest = new ObjectMapper().readValue(request.getInputStream(), BillPaymentRequest.class);

            try {
                FlipResponse response = paymentFlip.billProses(billPaymentRequest);

                if (response == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new FlipResponse("ERROR", "Response from Flip is null"));
                }

                // Hardcode token
                String token = flipConfig.getCallbackValidasiToken();

                String paymentGatewayId = billPaymentRequest.getPaymentGatewayId();
                String pgTransactionId = paymentGatewayTransactionRepository.findLatestPendingTransactionByEscrowAccountId(billPaymentRequest.getEscrowAccountId()).get();
                ResponseEntity<?> billPaymentLinkResponse = billPaymentService.getBillPaymentLink(response.getLinkId(), response.getBillPayment().getId(), paymentGatewayId, pgTransactionId);
                ObjectMapper objectMapper = new ObjectMapper();
                String dataJson = objectMapper.writeValueAsString(billPaymentLinkResponse.getBody());

                // Langsung panggil callback secara internal
                paymentFlip.callback(dataJson, token);

                return ResponseEntity.ok(response.getBillPayment().getReceiverBankAccount().getAccountNumber());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new FlipResponse("ERROR", e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Unsupported content type");
        }
    }

    @GetMapping("/read-all-payment")
    public BaseResponse<Page<BillPayment>> getAllPayment(
            @ModelAttribute BilPaymentSearch bilPaymentSearch,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<BillPayment> result = paymentFlip.findAllViewsByRequestId(bilPaymentSearch, pageable);
        return BaseResponse.ok(result);
    }

    @PutMapping("/update/{billId}")
    public BaseResponse<BillPaymentResponse> updateBill(
            @PathVariable String billId,
            @RequestBody BillPaymentRequest request) {

        BillPaymentResponse updatedBill = paymentFlip.updateBill(billId, request);
        return BaseResponse.ok(updatedBill);
    }

    @GetMapping("/get/{billId}")    
    public BaseResponse<BillPaymentResponse> getBillId(
            @PathVariable String billId
    ) {
        BillPaymentResponse getBillId = paymentFlip.getBillId(billId);
        return BaseResponse.ok(getBillId);
    }

    @GetMapping("/link/{linkId}")
    public ResponseEntity<?> getBillPaymentLink(@PathVariable String linkId) {
        return billPaymentService.getBillPaymentLink(linkId, "", "", "");
    }
}