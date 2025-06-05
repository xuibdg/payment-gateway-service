package com.core.payment_gateway_service.service;

import com.core.payment_gateway_service.entity.PaymentGatewayConfig;
import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import com.core.payment_gateway_service.enums.TransactionStatus;
import com.core.payment_gateway_service.repository.PaymentGatewayConfigRepository;
import com.core.payment_gateway_service.repository.PaymentGatewayTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@Slf4j
public class FlipReconciliationService {
    @Autowired
    private PaymentGatewayTransactionRepository paymentGatewayTransactionRepository;

    @Autowired
    private PaymentGatewayConfigRepository paymentGatewayConfigRepository;

    @Autowired
    private FlipTransactionStatusService flipTransactionStatusService;

    @Scheduled(fixedDelay = 60000)
    public void checkPendingTransactions() {

        log.info("Scheduler Start Running");

        List<PaymentGatewayTransaction> pendingTransaction =
                paymentGatewayTransactionRepository.findByStatus(TransactionStatus.PENDING_INITIATION);

        PaymentGatewayConfig config = paymentGatewayConfigRepository
                .findByPaymentGateway_GatewayCodeAndConfigName("FLIP", "api_key");

        if (config == null) {
            log.error("Api Key not found");
            return;
        }

        String apiKey = config.getConfigValue();

        for (PaymentGatewayTransaction tx : pendingTransaction) {
            try {
                updateTransactionStatus(tx, apiKey);
            } catch (Exception e) {
                log.error("Error processing transaction {}: {}", tx.getInternalReferenceId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void updateTransactionStatus(PaymentGatewayTransaction tx, String apiKey) {
        String linkId = tx.getExternalTransactionId();
        if (linkId == null) {
            log.warn("Link id ID not found in transaction {}", tx.getInternalReferenceId());
            return;
        } else {
            log.info("Link ID [{}] extracted from transaction [{}]", linkId, tx.getInternalReferenceId());
        }

        String flipStatus = flipTransactionStatusService.checkFlipStatus(linkId, apiKey);
        log.info("Transaction {} Flip status: {}", tx.getInternalReferenceId(), flipStatus);

        if ("SUCCESS".equalsIgnoreCase(flipStatus)) {
            tx.setStatus(TransactionStatus.SUCCESSFUL);
        } else if ("PENDING".equalsIgnoreCase(flipStatus)) {
            tx.setStatus(TransactionStatus.PENDING_INITIATION);
        } else if ("FAILED".equalsIgnoreCase(flipStatus) || "CANCELLED".equalsIgnoreCase(flipStatus)) {
            tx.setStatus(TransactionStatus.FAILED);
        } else {
            return;
        }

        log.info("saving new status" + flipStatus);
        paymentGatewayTransactionRepository.save(tx);
        log.info("After save, status in entity: {}", tx.getStatus());
    }

}


