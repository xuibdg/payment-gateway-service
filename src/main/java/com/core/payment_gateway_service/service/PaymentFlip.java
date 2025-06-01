package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.DTO.*;
import com.core.payment_gateway_service.config.FlipConfiguration;
import com.core.payment_gateway_service.entity.*;
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
import org.springframework.beans.factory.annotation.Autowired;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentFlip {


    private final BillPaymentRepository billPaymentRepository;
    private final RestTemplate restTemplate;
    private final FlipConfiguration flipConfig;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentGatewayTransactionRepository paymentGatewayTransactionRepository;
    private final PaymentGatewayConfigRepository paymentGatewayConfigRepository;
    private final EscrowAccountRepository escrowAccountRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final PaymentGatewayCallbackRepository paymentGatewayCallbackRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public FlipResponse billProses(BillPaymentRequest request) {
        String id = "JWT" + UUID.randomUUID();
        String billId = null;

        LocalDateTime expiredDate = LocalDateTime.now().plusHours(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = expiredDate.format(formatter);

        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount tidak boleh null");
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
                billId = response.getLinkId(); // <-- simpan ID-nya
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

                if (request.getPaymentGatewayId() != null) {
                    pgTx.setPaymentGatewayId(
                            paymentGatewayRepository.findById(request.getPaymentGatewayId())
                                    .orElseThrow(() -> new RuntimeException("Gateway not found"))
                    );
                }
                if (request.getTargetSavingAccountId() != null) {
                    pgTx.setTargetSavingAccountId(
                            savingAccountRepository.findById(request.getTargetSavingAccountId())
                                    .orElseThrow(() -> new RuntimeException("Gateway not found"))
                    );
                }

                if (request.getTargetLoanAccountId() != null) {
                    pgTx.setTargetLoanAccountId(
                            loanAccountRepository.findById(request.getTargetLoanAccountId())
                                    .orElseThrow(() -> new RuntimeException("Gateway not found"))
                    );
                }
                if (request.getTargetEscrowAccountId() != null) {
                    pgTx.setTargetEscrowAccountId(
                            escrowAccountRepository.findById(request.getTargetEscrowAccountId())
                                    .orElseThrow(() -> new RuntimeException("Gateway not found"))
                    );
                }


//                pgTx.setPaymentGatewayId(paymentGatewayRepository.findById(request.getPaymentGatewayId())
//                        .orElseThrow(() -> new RuntimeException("Gateway not found")));
//                pgTx.setTargetSavingAccountId(
//                         request.getTargetSavingAccountId() == null ? null :
//                                savingAccountRepository.findById(request.getTargetSavingAccountId())
//                                        .orElseThrow(() -> new RuntimeException("Saving account not found")));
//                pgTx.setTargetLoanAccountId(
//                        request.getTargetLoanAccountId() == null ? null :
//                                loanAccountRepository.findById(request.getTargetLoanAccountId())
//                                        .orElseThrow(() -> new RuntimeException("Loan account not found")));
//                pgTx.setTargetEscrowAccountId(
//                        request.getTargetEscrowAccountId() == null ? null :
//                                escrowAccountRepository.findById(request.getTargetEscrowAccountId())
//                                        .orElseThrow(() -> new RuntimeException("Escrow account not found")));
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

        // Ambil data bill/inquiry dari database
        BillPayment inquiry = billPaymentRepository.findByPaymentId(callbackData.getId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        // Cek apakah sudah selesai
        String currentStatus = inquiry.getPaymentStatus();
        if ("SUCCESSFUL".equalsIgnoreCase(currentStatus) ||
                "FAILED".equalsIgnoreCase(currentStatus) ||
                "CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Callback with this payment ID has already been processed with final status: " + currentStatus);
        }

        inquiry.setStatus(callbackData.getStatus());
        inquiry.setPaymentStatus(callbackData.getStatus());

        PaymentGatewayCallback callback = new PaymentGatewayCallback();
        callback.setRawPayload(dataJson);
        callback.setHeaders("token=" + token);
        callback.setProcessingStatus(CallbackProcessingStatus.RECEIVED);
        callback.setReceivedAt(Timestamp.from(Instant.now()));

        paymentGatewayCallbackRepository.save(callback);

        billPaymentRepository.save(inquiry);

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

