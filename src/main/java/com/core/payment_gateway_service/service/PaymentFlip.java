package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.config.FlipConfiguration;
import com.core.payment_gateway_service.dto.*;
import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.entity.Customer;
import com.core.payment_gateway_service.entity.PaymentGatewayCallback;
import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import com.core.payment_gateway_service.enums.CallbackProcessingStatus;
import com.core.payment_gateway_service.enums.TransactionStatus;
import com.core.payment_gateway_service.enums.TransactionType;
import com.core.payment_gateway_service.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentFlip {


    private final BillPaymentRepository billPaymentRepository;
    private final RestTemplate restTemplate;
    private final FlipConfiguration flipConfig;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentGatewayTransactionRepository paymentGatewayTransactionRepository;
    private final EscrowAccountRepository escrowAccountRepository;
    private final PaymentGatewayCallbackRepository paymentGatewayCallbackRepository;
    private final ObjectMapper objectMapper;
    private final CustomerRepository customerRepository;

    @Transactional
    public FlipResponse billProses(BillPaymentRequest request) {
        String id = "JWT" + UUID.randomUUID();
        String billId = null;

        LocalDateTime expiredDate = LocalDateTime.now().plusHours(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = expiredDate.format(formatter);


        Optional<Customer> customerOpt = customerRepository.findByEmailAndFullNameAndPhoneNumber(
                request.getSenderEmail(),
                request.getSenderName(),
                request.getCustomerPhone()
        );

        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer dengan kombinasi data tersebut tidak ditemukan");
        }

        if (request.getTitle() == null){
            throw new IllegalArgumentException("Title tidak boleh null");
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException("Type tidak boleh null");
        }

        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount tidak boleh null");
        }

        if (request.getStep() == null) {
            throw new IllegalArgumentException("Step tidak boleh null");
        }

        if (request.getSenderName() == null || request.getSenderName().trim().isEmpty()) {
            throw new IllegalArgumentException("Sender name tidak boleh kosong");
        }

        if (request.getSenderEmail() == null || request.getSenderEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Sender email tidak boleh kosong");
        }

        if (request.getSenderBank() == null || request.getSenderBank().trim().isEmpty()) {
            throw new IllegalArgumentException("Sender bank tidak boleh kosong");
        }

        if (request.getSenderBankType() == null || request.getSenderBankType().trim().isEmpty()) {
            throw new IllegalArgumentException("Sender bank type tidak boleh kosong");
        }


        if (request.getCustomerAddress() == null || request.getCustomerAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer address tidak boleh kosong");
        }

        if (request.getExpiredDate() != null) {
            expiredDate = request.getExpiredDate();
            formattedDate = expiredDate.format(formatter);
        }

        MultiValueMap<String, String> requestFlip = new LinkedMultiValueMap<>();
        requestFlip.add("title", request.getTitle());
        requestFlip.add("type", request.getType());
        requestFlip.add("amount", (request.getAmount().toString()));
        requestFlip.add("step", (request.getStep().toString()));
        requestFlip.add("sender_name", request.getSenderName());
        requestFlip.add("sender_email", request.getSenderEmail());
        requestFlip.add("sender_bank", request.getSenderBank());
        requestFlip.add("sender_bank_type", request.getSenderBankType());
        requestFlip.add("expired_date", formattedDate);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", flipConfig.getToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestFlip, headers);


        try {
            FlipResponse response = restTemplate.postForObject(
                    flipConfig.getApiKeyBillPaymentUrl(),
                    entity,
                    FlipResponse.class
            );

            if (response != null && response.getBillPayment() != null) {
                billId = response.getLinkId();
                BillPayment bill = new BillPayment();
                bill.setId(response.getLinkId());
                bill.setTitle(request.getTitle());
                bill.setType(request.getType());
                bill.setSenderName(request.getSenderName());
                bill.setSenderEmail(request.getSenderEmail());
                bill.setAmount(response.getBillPayment().getAmount());
                bill.setSenderBank(response.getBillPayment().getSender_bank());
                bill.setSenderBankType(request.getSenderBankType());
                bill.setPaymentId(response.getBillPayment().getId());
                bill.setPaymentAmount(response.getBillPayment().getAmount());
                bill.setPaymentStatus(response.getBillPayment().getStatus());
                bill.setAccountHolder(response.getBillPayment().getReceiverBankAccount().getAccountHolder());
                bill.setStatus(response.getStatus());
                bill.setAccountNumber(response.getBillPayment().getReceiverBankAccount().getAccountNumber());
                bill.setBankCode(response.getBillPayment().getReceiverBankAccount().getBankCode());
                bill.setCustomerPhone(request.getCustomerPhone());
                bill.setCustomerAddress(request.getCustomerAddress());
                bill.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                bill.setExpiredDate(expiredDate);

                billPaymentRepository.save(bill);


                PaymentGatewayTransaction pgTx = new PaymentGatewayTransaction();
                pgTx.setInternalReferenceId(id);
                pgTx.setExternalTransactionId(response.getLinkId());
                pgTx.setTransactionType(TransactionType.INBOUND_FUNDING);
                pgTx.setAmount(request.getAmount());
                pgTx.setCurrencyCode("IDR");
                pgTx.setStatus(TransactionStatus.PENDING_INITIATION);
                pgTx.setInitiatedAt(Timestamp.valueOf(LocalDateTime.now()));
                pgTx.setCreatedAt( Timestamp.valueOf(LocalDateTime.now()));
                pgTx.setExpiresAt(Timestamp.valueOf(expiredDate));
                pgTx.setRequestPayload(objectMapper.convertValue(request, new TypeReference<>() {}));
                pgTx.setResponsePayload(objectMapper.convertValue(response, new TypeReference<>() {}));

                Map<String, Object> methodDetail = new HashMap<>();
                methodDetail.put("sender_bank", request.getSenderBank());
                methodDetail.put("bank_type", request.getSenderBankType());
                methodDetail.put("sender_name", request.getSenderName());
                pgTx.setPaymentMethodDetails(methodDetail);

                pgTx.setPaymentGatewayId(paymentGatewayRepository.findById(request.getPaymentGatewayId()).
                        orElseThrow(() -> new RuntimeException("Payment Gateway Id not found")));

                pgTx.setEscrowAccountId(escrowAccountRepository.findById(request.getEscrowAccountId())
                        .orElseThrow(() -> new RuntimeException("Escrow Account Id not found")));

                    paymentGatewayTransactionRepository.save(pgTx);

                return response;
            } else {
                return new FlipResponse("Failed", "Tidak ada response dari Flip");
            }
        } catch (Exception e) {
            log.error("Error saat create bill ke Flip: {}", e.getMessage());

            if (billId != null) {
                billPaymentRepository.findById(billId).ifPresent(bill -> {
                    bill.setStatus("Failed");
                    billPaymentRepository.save(bill);
                });
            }
            throw new RuntimeException("Gagal membuat bill: " + e.getMessage(), e);
        }
    }

    public BillPaymentResponse callback(String dataJson, String token) throws JsonProcessingException {
        // Validasi token callback
        if (!token.equals(flipConfig.getCallbackValidasiToken())) {
            throw new RuntimeException("Invalid token from callback");
        }


        // Konfigurasi mapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Parse payload JSON ke DTO
        BillPaymentRequest callbackData = mapper.readValue(dataJson, BillPaymentRequest.class);
        PaymentGatewayCallbackRequest callbackRequest = mapper.readValue(dataJson, PaymentGatewayCallbackRequest.class);
        PaymentGatewayTransactionRequest paymentRequest = mapper.readValue(dataJson, PaymentGatewayTransactionRequest.class);


        if (callbackData.getSenderName() != null && !callbackData.getSenderName().trim().isEmpty()) {
            if (!customerRepository.findByFullName(callbackData.getSenderName()).isPresent()) {
                throw new RuntimeException("Customer dengan name " + callbackData.getSenderName() + " tidak ditemukan");
            }
        }

        if (callbackData.getSenderEmail() != null && !callbackData.getSenderEmail().trim().isEmpty()) {
            if (!customerRepository.findByEmail(callbackData.getSenderEmail()).isPresent()) {
                throw new RuntimeException("Customer dengan email " + callbackData.getSenderEmail() + " tidak ditemukan");
            }
        }

        // Ambil data bill/inquiry dari database
        BillPayment inquiry = billPaymentRepository.findByPaymentId(callbackData.getId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        PaymentGatewayTransaction paymentGatewayTransaction = paymentGatewayTransactionRepository.findByExternalTransactionId(paymentRequest.getExternalTransactionId())
                .orElseThrow(() -> new RuntimeException("External Transaction Id not found"));

        String normalizedStatus = callbackData.getStatus() != null ? callbackData.getStatus().trim().toUpperCase() : "";

        Set<String> FINAL_STATUSES = Set.of("SUCCESSFUL", "FAILED", "CANCELLED");

        if (inquiry.getPaymentStatus() != null &&
                FINAL_STATUSES.contains(inquiry.getPaymentStatus().trim().toUpperCase())) {
            throw new RuntimeException("BillPayment already has a final status: " + inquiry.getPaymentStatus());
        }

        if (paymentGatewayTransaction.getStatus() != null &&
                FINAL_STATUSES.contains(paymentGatewayTransaction.getStatus().name().toUpperCase())) {
            throw new RuntimeException("Transaction already has a final status: " + paymentGatewayTransaction.getStatus());
        }

// Set ke BillPayment
        inquiry.setStatus(normalizedStatus);
        inquiry.setPaymentStatus(normalizedStatus);

// Set ke PG Transaction
        switch (normalizedStatus) {
            case "SUCCESSFUL":
                paymentGatewayTransaction.setStatus(TransactionStatus.SUCCESSFUL);
                break;
            case "FAILED":
                paymentGatewayTransaction.setStatus(TransactionStatus.FAILED);
                break;
            case "CANCELLED":
                paymentGatewayTransaction.setStatus(TransactionStatus.CANCELLED);
                break;
            default:
                paymentGatewayTransaction.setStatus(TransactionStatus.EXPIRED);
                break;
        }

        PaymentGatewayCallback callback = new PaymentGatewayCallback();
        callback.setRawPayload(dataJson);
        callback.setHeaders("token=" + token);
        callback.setProcessingStatus(CallbackProcessingStatus.RECEIVED);
        callback.setReceivedAt(Timestamp.from(Instant.now()));
        callback.setExternalTransactionId(callbackData.getBillLinkId());

        callback.setPaymentGatewayId(paymentGatewayRepository.findById(callbackRequest.getPaymentGatewayId())
                        .orElseThrow(() -> new RuntimeException("Payment Gateway Id not found")));

        callback.setPgTransactionId(paymentGatewayTransactionRepository.findById(callbackRequest.getPgTransactionId())
                        .orElseThrow(() -> new RuntimeException("Payment Gateway Transaction Id not found")));

        paymentGatewayTransaction.setCompletedAt(Timestamp.from(Instant.now()));


        paymentGatewayCallbackRepository.save(callback);

        billPaymentRepository.save(inquiry);

        paymentGatewayTransactionRepository.save(paymentGatewayTransaction);
        return mapToResponse(inquiry);
    }


    private BillPaymentResponse mapToResponse(BillPayment entity) {
        return BillPaymentResponse.builder()
                .status(entity.getPaymentStatus())
                .senderName(entity.getSenderName())
                .senderBank(entity.getSenderBank())
                .build();
    }

    public Page<BillPayment> findAllViewsByRequestId(BilPaymentSearch request, Pageable pageable) {
        Specification<BillPayment> spec = SpecificationHelper.bySearch(request);
        return billPaymentRepository.findAll(spec, pageable);
    }

    public BillPaymentResponse getBillId(String billId) {
        BillPayment inquiry = billPaymentRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill Id not found"));


        return mapToResponse(inquiry);
    }

    @Transactional
    public BillPaymentResponse updateBill(String billId, BillPaymentRequest request) {
        BillPayment bill = billPaymentRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        BillPayment inquiry = billPaymentRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        String currentStatus = inquiry.getPaymentStatus();
        if ("SUCCESSFUL".equalsIgnoreCase(currentStatus) ||
                "FAILED".equalsIgnoreCase(currentStatus) ||
                "CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Tidak Bisa Di Ubah Karna Status: " + currentStatus);
        }

        if (request.getTitle() != null) bill.setTitle(request.getTitle());
        if (request.getType() != null) bill.setType(request.getType());
        if (request.getExpiredDate() != null) bill.setExpiredDate(request.getExpiredDate());
        if (request.getAmount() != null) bill.setAmount(request.getAmount());
        if (request.getStatus() != null) bill.setStatus(request.getStatus());
        if (request.getSenderName() != null) bill.setSenderName(request.getSenderName());
        if (request.getStatus() != null) bill.setPaymentStatus(request.getStatus());
        if (request.getExpiredDate() != null) bill.setExpiredDate(request.getExpiredDate());


        bill = billPaymentRepository.save(bill);

        return mapToResponse(bill);
    }


}

