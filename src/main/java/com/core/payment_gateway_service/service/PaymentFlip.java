package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.DTO.*;
import com.core.payment_gateway_service.config.FlipConfiguration;
import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.entity.EscrowAccount;
import com.core.payment_gateway_service.entity.PaymentGateway;
import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import com.core.payment_gateway_service.enums.TransactionStatus;
import com.core.payment_gateway_service.enums.TransactionType;
import com.core.payment_gateway_service.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional
    public FlipResponse billProses(BillPaymentRequest request) {
        String id = "JWT" + UUID.randomUUID();

        LocalDateTime expiredDate = LocalDateTime.now().plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = expiredDate.format(formatter);


        MultiValueMap<String, String> requestFlip = new LinkedMultiValueMap<>();
        requestFlip.add("title", request.getTitle());
        requestFlip.add("type", request.getType());
        requestFlip.add("amount", request.getAmount().toString());
        requestFlip.add("step", request.getStep().toString());
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

//                PaymentGateway paymentGateway = PaymentGateway.builder()
//                        .paymentGatewayId(id)
//                        .gatewayName(request.getSenderName())
//                        .gatewayCode("FLIP")
//                        .isActive(true)
//                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
//                        .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
//                        .build();
//                paymentGatewayRepository.save(paymentGateway);

                PaymentGatewayTransaction paymentGatewayTransaction =  new PaymentGatewayTransaction();
                paymentGatewayTransaction.setPaymentGateway(paymentGatewayTransaction.getPaymentGateway());
                paymentGatewayTransaction.setPgTransactionId(response.getBillPayment().getId());
                paymentGatewayTransaction.setAmount(BigDecimal.valueOf(response.getBillPayment().getAmount()));
                paymentGatewayTransaction.setCreatedAt(response.getBillPayment().getCreated_at());
               // paymentGatewayTransaction.setExpiresAt(response.getBillPayment().get);
                paymentGatewayTransaction.setCurrencyCode("IDR");
                paymentGatewayTransaction.setExternalTransactionId(response.getLinkId());
                paymentGatewayTransaction.setInternalReferenceId(id);
              //paymentGatewayTransaction.setPaymentMethodDetails(response.getBillPayment().getReceiverBankAccount().getBankCode());
              // paymentGatewayTransaction.setRequestPayload();
                paymentGatewayTransaction.setStatus(TransactionStatus.PENDING_INITIATION);
                paymentGatewayTransaction.setTargetEscrowAccountId(paymentGatewayTransaction.getTargetEscrowAccountId());
                paymentGatewayTransaction.setTargetLoanAccountId(paymentGatewayTransaction.getTargetLoanAccountId());
                paymentGatewayTransaction.setTargetSavingAccountId(paymentGatewayTransaction.getTargetSavingAccountId());
                paymentGatewayTransaction.setTransactionType(TransactionType.INBOUND_FUNDING);
                paymentGatewayTransaction.setInitiatedAt(Timestamp.valueOf(LocalDateTime.now()));
                paymentGatewayTransactionRepository.save(paymentGatewayTransaction);


                return response;
            } else {
                return new FlipResponse("Failed", "Tidak ada response dari Flip");
            }
        } catch (Exception e) {
            log.error("Error saat create bill ke Flip: {}", e.getMessage());

            billPaymentRepository.findById(id).ifPresent(bill -> {
                bill.setPaymentStatus("Failed");
                billPaymentRepository.save(bill);
            });

            return new FlipResponse("ERROR", "Gagal membuat bill: " + e.getMessage());
        }
    }

    public BillPaymentResponse callback(String dataJson, String token) throws JsonProcessingException {
        if (!token.equals(flipConfig.getCallbackValidasiToken())) {
            throw new RuntimeException ("Invalid token from callback");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        BillPaymentRequest callbackData = mapper.readValue(dataJson, BillPaymentRequest.class);

        BillPayment inquiry = billPaymentRepository.findByPaymentId(callbackData.getId())
                .orElseThrow(() -> new RuntimeException ("Inquiry not found"));

        String currentStatus = inquiry.getPaymentStatus();
        if ("SUCCESSFUL".equalsIgnoreCase(currentStatus) ||
                "FAILED".equalsIgnoreCase(currentStatus) ||
                "CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Callback with this payment ID has already been processed with final status: " + currentStatus);
        }

        inquiry.setStatus(callbackData.getStatus());
        inquiry.setPaymentStatus(callbackData.getStatus());

        return mapToResponse(billPaymentRepository.save(inquiry));
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

