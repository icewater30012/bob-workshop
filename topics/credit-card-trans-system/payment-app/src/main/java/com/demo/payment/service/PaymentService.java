package com.demo.payment.service;

import com.demo.payment.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();

    // Test card numbers
    private static final String VISA_TEST = "4263970000005262";
    private static final String MASTERCARD_TEST = "5425230000004415";
    private static final String AMEX_TEST = "374101000000608";

    public PaymentService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse authorize(AuthorizeRequest request) {
        log.info("Processing authorization for card ending in {}", 
                 maskCardNumber(request.getCardNumber()));

        // Simulate processing delay
        simulateDelay();

        // Validate card expiry
        if (isCardExpired(request.getExpiry())) {
            return createDeclinedTransaction(request, "EXPIRED_CARD", "Card has expired");
        }

        // Simulate random declines (10% chance)
        if (random.nextInt(100) < 10) {
            return createDeclinedTransaction(request, "DECLINED", "Transaction declined by issuer");
        }

        // Simulate insufficient funds (5% chance)
        if (random.nextInt(100) < 5) {
            return createDeclinedTransaction(request, "INSUFFICIENT_FUNDS", "Insufficient funds");
        }

        // Create successful authorization
        Transaction transaction = Transaction.builder()
                .cardNumber(request.getCardNumber())
                .cardType(detectCardType(request.getCardNumber()))
                .amount(request.getAmount())
                .status(TransactionStatus.AUTHORIZED)
                .responseCode("APPROVED")
                .responseMessage("Authorization successful")
                .authorizedAmount(request.getAmount())
                .capturedAmount(BigDecimal.ZERO)
                .refundedAmount(BigDecimal.ZERO)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Authorization successful: {}", transaction.getId());

        return TransactionResponse.fromTransaction(transaction);
    }

    @Transactional
    public TransactionResponse capture(CaptureRequest request) {
        log.info("Processing capture for transaction {}", request.getTransactionId());

        simulateDelay();

        Optional<Transaction> optionalTransaction = transactionRepository.findById(request.getTransactionId());
        if (!optionalTransaction.isPresent()) {
            throw new IllegalArgumentException("Transaction not found: " + request.getTransactionId());
        }

        Transaction transaction = optionalTransaction.get();

        if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new IllegalStateException("Transaction must be in AUTHORIZED status to capture");
        }

        BigDecimal authorizedAmount = transaction.getAuthorizedAmount();
        if (request.getAmount().compareTo(authorizedAmount) > 0) {
            throw new IllegalArgumentException("Capture amount cannot exceed authorized amount");
        }

        transaction.setStatus(TransactionStatus.CAPTURED);
        transaction.setCapturedAmount(request.getAmount());
        transaction.setResponseCode("CAPTURED");
        transaction.setResponseMessage("Capture successful");

        transaction = transactionRepository.save(transaction);
        log.info("Capture successful: {}", transaction.getId());

        return TransactionResponse.fromTransaction(transaction);
    }

    @Transactional
    public TransactionResponse refund(RefundRequest request) {
        log.info("Processing refund for transaction {}", request.getTransactionId());

        simulateDelay();

        Optional<Transaction> optionalTransaction = transactionRepository.findById(request.getTransactionId());
        if (!optionalTransaction.isPresent()) {
            throw new IllegalArgumentException("Transaction not found: " + request.getTransactionId());
        }

        Transaction transaction = optionalTransaction.get();

        if (transaction.getStatus() != TransactionStatus.CAPTURED) {
            throw new IllegalStateException("Transaction must be in CAPTURED status to refund");
        }

        BigDecimal capturedAmount = transaction.getCapturedAmount();
        BigDecimal alreadyRefunded = transaction.getRefundedAmount() != null ? 
                                     transaction.getRefundedAmount() : BigDecimal.ZERO;
        BigDecimal availableForRefund = capturedAmount.subtract(alreadyRefunded);

        if (request.getAmount().compareTo(availableForRefund) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds available amount");
        }

        transaction.setStatus(TransactionStatus.REFUNDED);
        transaction.setRefundedAmount(alreadyRefunded.add(request.getAmount()));
        transaction.setResponseCode("REFUNDED");
        transaction.setResponseMessage("Refund successful");

        transaction = transactionRepository.save(transaction);
        log.info("Refund successful: {}", transaction.getId());

        return TransactionResponse.fromTransaction(transaction);
    }

    @Cacheable(value = "transactions", key = "#id")
    public TransactionResponse getTransaction(String id) {
        log.info("Fetching transaction: {}", id);
        
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (!optionalTransaction.isPresent()) {
            throw new IllegalArgumentException("Transaction not found: " + id);
        }

        return TransactionResponse.fromTransaction(optionalTransaction.get());
    }

    public List<TransactionResponse> getRecentTransactions() {
        log.info("Fetching recent transactions");
        
        List<Transaction> transactions = transactionRepository.findTop20ByOrderByCreatedAtDesc();
        return transactions.stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public void clearCache() {
        log.info("Cache cleared");
    }

    private TransactionResponse createDeclinedTransaction(AuthorizeRequest request, 
                                                          String code, String message) {
        Transaction transaction = Transaction.builder()
                .cardNumber(request.getCardNumber())
                .cardType(detectCardType(request.getCardNumber()))
                .amount(request.getAmount())
                .status(TransactionStatus.DECLINED)
                .responseCode(code)
                .responseMessage(message)
                .authorizedAmount(BigDecimal.ZERO)
                .capturedAmount(BigDecimal.ZERO)
                .refundedAmount(BigDecimal.ZERO)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transaction declined: {} - {}", transaction.getId(), message);

        return TransactionResponse.fromTransaction(transaction);
    }

    private String detectCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "VISA";
        } else if (cardNumber.startsWith("5")) {
            return "MASTERCARD";
        } else if (cardNumber.startsWith("3")) {
            return "AMEX";
        }
        return "UNKNOWN";
    }

    private boolean isCardExpired(String expiry) {
        try {
            String[] parts = expiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = 2000 + Integer.parseInt(parts[1]);
            
            LocalDateTime now = LocalDateTime.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            
            return year < currentYear || (year == currentYear && month < currentMonth);
        } catch (Exception e) {
            return true; // Treat invalid format as expired
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }

    private void simulateDelay() {
        try {
            // Random delay between 200-500ms
            Thread.sleep(200 + random.nextInt(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Made with Bob
