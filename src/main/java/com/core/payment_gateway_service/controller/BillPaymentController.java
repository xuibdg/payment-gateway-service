package com.core.payment_gateway_service.controller;

import com.core.payment_gateway_service.dto.BaseResponse;
import com.core.payment_gateway_service.dto.BilPaymentSearch;
import com.core.payment_gateway_service.dto.BillPaymentRequest;
import com.core.payment_gateway_service.dto.BillPaymentResponse;
import com.core.payment_gateway_service.dto.FlipResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}