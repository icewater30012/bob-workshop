package com.uitc.controller;

import com.uitc.model.Transaction;
import com.uitc.service.TransactionService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 交易控制器
 *
 * @author IBM Bob Workshop
 * @version 1.0.0
 */
// @Tag(name = "交易管理", description = "信用卡交易資料查詢與統計 API")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    // @Operation(
    //     summary = "查詢所有交易",
    //     description = "取得系統中所有的信用卡交易記錄"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "成功取得交易列表",
    //         content = @Content(schema = @Schema(implementation = Transaction.class))
    //     )
    // })
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    // @Operation(
    //     summary = "查詢單筆交易",
    //     description = "根據交易 ID 查詢特定交易的詳細資訊"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "成功取得交易資料",
    //         content = @Content(schema = @Schema(implementation = Transaction.class))
    //     ),
    //     @ApiResponse(responseCode = "404", description = "找不到指定的交易")
    // })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(
            // @Parameter(description = "交易 ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    // @Operation(
    //     summary = "查詢指定卡片的交易",
    //     description = "取得特定信用卡的所有交易歷史記錄"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "成功取得該卡片的交易列表",
    //         content = @Content(schema = @Schema(implementation = Transaction.class))
    //     )
    // })
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCard(
            // @Parameter(description = "信用卡 ID", required = true, example = "1")
            @PathVariable Long cardId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCardId(cardId));
    }
    
    // @Operation(
    //     summary = "查詢最近的交易",
    //     description = "取得指定時間範圍內的最近交易記錄，用於即時監控"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "成功取得最近交易列表",
    //         content = @Content(schema = @Schema(implementation = Transaction.class))
    //     )
    // })
    @GetMapping("/recent")
    public ResponseEntity<List<Transaction>> getRecentTransactions(
            // @Parameter(description = "查詢最近 N 小時的交易", example = "24")
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(transactionService.getRecentTransactions(hours));
    }
    
    // @Operation(
    //     summary = "查詢高額交易",
    //     description = "取得超過指定金額門檻的交易記錄，用於風險監控"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "成功取得高額交易列表",
    //         content = @Content(schema = @Schema(implementation = Transaction.class))
    //     )
    // })
    @GetMapping("/high-amount")
    public ResponseEntity<List<Transaction>> getHighAmountTransactions(
            // @Parameter(description = "金額門檻（新台幣）", example = "50000")
            @RequestParam(defaultValue = "50000") BigDecimal threshold) {
        return ResponseEntity.ok(transactionService.getHighAmountTransactions(threshold));
    }
    
    // @Operation(
    //     summary = "取得交易統計",
    //     description = "取得交易的統計資料，包含總交易數、總金額、核准數量、平均金額等"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "成功取得統計資料"
    //     )
    // })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<Transaction> allTransactions = transactionService.getAllTransactions();
        
        BigDecimal totalAmount = allTransactions.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long approvedCount = allTransactions.stream()
            .filter(t -> t.getStatus() != null && "APPROVED".equals(t.getStatus().name()))
            .count();
        
        return ResponseEntity.ok(Map.of(
            "totalTransactions", allTransactions.size(),
            "totalAmount", totalAmount,
            "approvedCount", approvedCount,
            "averageAmount", allTransactions.isEmpty() ?
                BigDecimal.ZERO :
                totalAmount.divide(BigDecimal.valueOf(allTransactions.size()), 2, RoundingMode.HALF_UP)
        ));
    }
}

// Made with Bob
