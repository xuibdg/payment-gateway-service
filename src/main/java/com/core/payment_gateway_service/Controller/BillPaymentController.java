package com.core.payment_gateway_service.Controller;

import com.core.payment_gateway_service.DTO.*;
import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.service.PaymentFlip;
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


    @GetMapping("/hallo")
    public String hello() {
        log.info("Hello endpoint called");
        return "Hello, this is a test endpoint!";
    }

    @PostMapping("/test")
    public String test(@RequestBody Map<String, String> request) {
        log.info("Test endpoint called with request: {}", request);
        return "Test endpoint received: " + request;
    }

    @PutMapping("/test-update")
    public String testUpdate(@RequestBody Map<String, String> request) {
        log.info("Test update endpoint called with request: {}", request);
        return "Test update endpoint received: " + request;
    }

    @DeleteMapping("/test-delete")
    public String testDelete(@RequestParam String id) {
        if (id == null || id.isEmpty()) {
            try {
                log.info("Test delete endpoint called with id: {}", id);
                return "Test delete endpoint received id: " + id;
            } catch (Exception a) {
                log.error("Error in test delete endpoint: {}", a.getMessage());
                return "Error in test delete endpoint: " + a.getMessage();
            }
        }
        log.info("Test delete endpoint called with id: {}", id);
        return "Test delete endpoint received id: " + id;
    }
}