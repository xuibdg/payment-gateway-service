package com.core.payment_gateway_service.controller;

import com.core.payment_gateway_service.dto.*;
import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.repository.PaymentGatewayCallbackRepository;
import com.core.payment_gateway_service.repository.PaymentGatewayTransactionRepository;
import com.core.payment_gateway_service.service.PaymentFlip;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bill-payment")
public class BillPaymentController {

    private final PaymentFlip billPaymentService;

    @PostMapping(value = "/process-bill-payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlipResponse> processBillPayment(@RequestBody BillPaymentRequest request) {
        try {
            FlipResponse response = billPaymentService.billProses(request);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new FlipResponse("ERR9OR", "Response from Flip is null"));
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
            billPaymentService.callback(dataJson, token);
            return "OK";
        } catch (SecurityException se) {
            log.warn("Unauthorized callback received: {}", se.getMessage());
            return "Unauthorized";
        } catch (Exception e) {
            log.error("Callback error: {}", e.getMessage(), e);
            return "Error";
        }
    }
//    @PostMapping("test-clbk")
//    public ResponseEntity<Map<String, Object>> handlingCallback(
//            @RequestHeader("X-Callback-Token") String token,
//            @RequestBody String rawJsonBody) {
//
//        Map<String, Object> response = billPaymentService.handleCallbacks(rawJsonBody, token);
//        HttpStatus status = "SUCCESS".equals(response.get("status"))
//                ? HttpStatus.OK
//                : HttpStatus.BAD_REQUEST;
//
//        return ResponseEntity.status(status).body(response);
//    }
    @PostMapping(value = "/process-and-callback-parallel", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CombinedResponse> processAndCallbackParallel(
                                                                       @Valid @RequestBody CombinedRequest request) {
        Map<String, Object> response = billPaymentService.handleCallbacks(request.getCallbackData(), request.getCallbackToken());

        HttpStatus status = "SUCCESS".equals(response.get("status"))
                ? HttpStatus.OK
                : HttpStatus.BAD_REQUEST;
        ResponseEntity.status(status);

        try {
            CompletableFuture<FlipResponse> paymentFuture = CompletableFuture.supplyAsync(() ->
                    billPaymentService.billProses(BillPaymentRequest.builder()
                            .title(request.getTitle())
                            .type(request.getType())
                            .amount(request.getAmount())
                            .step(request.getStep())
                            .senderBank(request.getSenderBank())
                            .senderBankType(request.getSenderBankType())
                            .senderName(request.getSenderName())
                            .senderEmail(request.getSenderEmail())
                            .customerPhone(request.getCustomerPhone())
                            .customerAddress(request.getCustomerAddress())
                            .paymentGatewayId(request.getPaymentGatewayId())
                            .escrowAccountId(request.getEscrowAccountId())
                            .build()));

            CompletableFuture<String> callbackFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    billPaymentService.callback(request.getCallbackData(), request.getCallbackToken());
                    //billPaymentService.callback(dataJson, token);
                    return "OK";
                } catch (SecurityException se) {
                    log.warn("Unauthorized callback received: {}", se.getMessage());
                    return "Unauthorized";
                } catch (Exception e) {
                    log.error("Callback error: {}", e.getMessage(), e);
                    return "Error";
                }
            });

            CompletableFuture.allOf(paymentFuture, callbackFuture).join();

            FlipResponse paymentResponse = paymentFuture.get();
            String callbackResponse = callbackFuture.get();

            return ResponseEntity.ok(new CombinedResponse(
                    paymentResponse,
                    callbackResponse,
                    "SUCCESS",
                    "Both operations completed in parallel"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CombinedResponse(null, null, "ERROR", e.getMessage()));
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
        Page<BillPayment> result = billPaymentService.findAllViewsByRequestId(bilPaymentSearch, pageable);
        return BaseResponse.ok(result);
    }

    @PutMapping("/update/{billId}")
    public BaseResponse<BillPaymentResponse> updateBill(
            @PathVariable String billId,
            @RequestBody BillPaymentRequest request) {

        BillPaymentResponse updatedBill = billPaymentService.updateBill(billId, request);
        return BaseResponse.ok(updatedBill);
    }

    @GetMapping("/get/{billId}")    
    public BaseResponse<BillPaymentResponse> getBillId(
            @PathVariable String billId
    ) {
        BillPaymentResponse getBillId = billPaymentService.getBillId(billId);
        return BaseResponse.ok(getBillId);
    }
}