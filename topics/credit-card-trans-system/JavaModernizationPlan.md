# Java 11 到 Java 17 現代化計劃

## 專案概述
**專案名稱**: Payment Processing Application  
**當前版本**: Java 11 + Spring Boot 2.7.18  
**目標版本**: Java 17 + Spring Boot 3.x  
**評估日期**: 2026-06-10

---

## 1. 變更摘要

### 1.1 核心變更
- **Java 版本**: 11 → 17
- **Spring Boot 版本**: 2.7.18 → 3.2.x (最新穩定版)
- **Jakarta EE 遷移**: javax.* → jakarta.*
- **依賴項更新**: 所有 Spring Boot 管理的依賴項
- **建置工具**: Maven 3.8.6+ (支援 Java 17)
- **容器映像**: eclipse-temurin-11 → eclipse-temurin-17

### 1.2 受影響的檔案統計
- **POM 檔案**: 1 個 (pom.xml)
- **Java 原始檔**: 13 個
- **配置檔**: 2 個 (application.properties, Dockerfile)
- **Kubernetes 配置**: 1 個 (deployment.yaml)

---

## 2. 已棄用/移除的 API 需要替換

### 2.1 Jakarta EE 命名空間變更 (高優先級)
**影響範圍**: 所有使用 javax.* 套件的檔案

#### 受影響的檔案:
1. **Transaction.java** (第 3 行)
   - `javax.persistence.*` → `jakarta.persistence.*`
   - 影響: @Entity, @Table, @Id, @Column, @Enumerated, @PrePersist, @PreUpdate

2. **AuthorizeRequest.java** (第 3-6 行)
   - `javax.validation.constraints.*` → `jakarta.validation.constraints.*`
   - 影響: @NotBlank, @NotNull, @Pattern, @DecimalMin

3. **CaptureRequest.java** (第 3-5 行)
   - `javax.validation.constraints.*` → `jakarta.validation.constraints.*`

4. **RefundRequest.java** (第 3-5 行)
   - `javax.validation.constraints.*` → `jakarta.validation.constraints.*`

5. **PaymentController.java** (第 10 行)
   - `javax.validation.Valid` → `jakarta.validation.Valid`

**風險等級**: 🔴 高 - 這是強制性變更，應用程式無法在不修改的情況下編譯

### 2.2 Optional.isPresent() 模式 (中優先級)
**影響範圍**: PaymentService.java

#### 當前模式 (Java 11):
```java
Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
if (!optionalTransaction.isPresent()) {
    throw new IllegalArgumentException("Transaction not found");
}
Transaction transaction = optionalTransaction.get();
```

**建議模式 (Java 17)**:
```java
Transaction transaction = transactionRepository.findById(id)
    .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
```

**受影響位置**:
- PaymentService.java: 第 82-87 行 (capture 方法)
- PaymentService.java: 第 115-120 行 (refund 方法)
- PaymentService.java: 第 150-153 行 (getTransaction 方法)

**風險等級**: 🟡 中 - 非強制性，但改進程式碼可讀性

---

## 3. Java 17 新功能改進機會

### 3.1 Records (記錄類別) - 高價值

#### 3.1.1 請求 DTO 轉換為 Records
**候選類別**: AuthorizeRequest, CaptureRequest, RefundRequest

**當前實作** (AuthorizeRequest.java - 72 行):
```java
public class AuthorizeRequest {
    @NotBlank private String cardNumber;
    @NotBlank private String expiry;
    @NotBlank private String cvv;
    @NotNull private BigDecimal amount;
    // + 建構子 + getters + setters
}
```

**建議實作** (使用 Record):
```java
public record AuthorizeRequest(
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,19}", message = "Invalid card number format")
    String cardNumber,
    
    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiry must be in MM/YY format")
    String expiry,
    
    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    String cvv,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount
) {}
```

**優勢**:
- 減少 ~40 行樣板程式碼 (每個類別)
- 自動產生 equals(), hashCode(), toString()
- 不可變性保證資料完整性
- 更清晰的意圖表達

**程式碼減少**:
- AuthorizeRequest: 72 → ~20 行 (減少 72%)
- CaptureRequest: 44 → ~15 行 (減少 66%)
- RefundRequest: 44 → ~15 行 (減少 66%)

#### 3.1.2 回應 DTO 轉換為 Record
**候選類別**: TransactionResponse

**建議實作**:
```java
public record TransactionResponse(
    String id,
    String cardNumber,
    String cardType,
    BigDecimal amount,
    TransactionStatus status,
    String responseCode,
    String responseMessage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    BigDecimal authorizedAmount,
    BigDecimal capturedAmount,
    BigDecimal refundedAmount
) {
    public static TransactionResponse fromTransaction(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            maskCardNumber(transaction.getCardNumber()),
            transaction.getCardType(),
            transaction.getAmount(),
            transaction.getStatus(),
            transaction.getResponseCode(),
            transaction.getResponseMessage(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getAuthorizedAmount(),
            transaction.getCapturedAmount(),
            transaction.getRefundedAmount()
        );
    }
    
    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
```

**程式碼減少**: 148 → ~35 行 (減少 76%)

**風險等級**: 🟢 低 - Records 與 Jackson 和 Bean Validation 完全相容

### 3.2 Sealed Classes (密封類別) - 中價值

**建議**: 第二階段實作 (非必要)

可用於增強 TransactionStatus，但需要重構 Transaction 實體和業務邏輯。

**風險等級**: 🟡 中

### 3.3 Switch Expressions - 中價值

#### detectCardType 方法改進
**當前實作** (PaymentService.java, 第 192-201 行):
```java
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
```

**建議實作** (使用 Switch Expression):
```java
private String detectCardType(String cardNumber) {
    return switch (cardNumber.charAt(0)) {
        case '4' -> "VISA";
        case '5' -> "MASTERCARD";
        case '3' -> "AMEX";
        default -> "UNKNOWN";
    };
}
```

**優勢**:
- 更簡潔 (9 行 → 6 行)
- 編譯時期完整性檢查
- 表達式語義更清晰

**風險等級**: 🟢 低 - 直接替換，無副作用

### 3.4 Enhanced NullPointerException Messages

**自動啟用**: Java 17 預設啟用 `-XX:+ShowCodeDetailsInExceptionMessages`

**優勢**:
- 更詳細的 NPE 錯誤訊息
- 更快的除錯
- 無需程式碼變更

---

## 4. pom.xml 變更需求

### 4.1 Java 版本更新
```xml
<properties>
    <!-- 從 -->
    <java.version>11</java.version>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    
    <!-- 改為 -->
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### 4.2 Spring Boot 版本升級
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <!-- 從 -->
    <version>2.7.18</version>
    <!-- 改為 -->
    <version>3.2.6</version> <!-- 或最新的 3.x 穩定版 -->
    <relativePath/>
</parent>
```

### 4.3 依賴項相容性

#### 自動處理 (由 Spring Boot 管理):
- ✅ spring-boot-starter-web
- ✅ spring-boot-starter-data-jpa
- ✅ spring-boot-starter-cache
- ✅ spring-boot-starter-actuator
- ✅ spring-boot-starter-validation
- ✅ spring-boot-starter-test
- ✅ h2database
- ✅ caffeine
- ✅ micrometer-registry-prometheus

### 4.4 Maven 外掛程式更新
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        
        <!-- 建議新增: Maven Compiler Plugin 明確配置 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <release>17</release>
                <compilerArgs>
                    <arg>-parameters</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 5. 工作量與風險評估

### 5.1 變更分類與工作量

| 變更類別 | 檔案數 | 預估工時 | 風險等級 | 優先級 |
|---------|-------|---------|---------|--------|
| **階段 1: 強制性變更** |
| Jakarta EE 命名空間 | 5 | 1 小時 | 🔴 高 | P0 |
| pom.xml 更新 | 1 | 0.5 小時 | 🔴 高 | P0 |
| Dockerfile 更新 | 1 | 0.5 小時 | 🟡 中 | P0 |
| 編譯與測試 | - | 2 小時 | 🔴 高 | P0 |
| **階段 1 小計** | **7** | **4 小時** | | |
| | | | | |
| **階段 2: 程式碼現代化** |
| Records 轉換 (DTOs) | 4 | 2 小時 | 🟢 低 | P1 |
| Optional 模式改進 | 1 | 1 小時 | 🟢 低 | P1 |
| Switch Expressions | 1 | 0.5 小時 | 🟢 低 | P2 |
| 程式碼審查與測試 | - | 2 小時 | 🟡 中 | P1 |
| **階段 2 小計** | **6** | **5.5 小時** | | |
| | | | | |
| **總計** | **13** | **9.5 小時** | | |

### 5.2 風險評估矩陣

#### 高風險項目 (需要特別注意):
1. **Jakarta EE 遷移** 🔴
   - **風險**: 編譯失敗、執行時期錯誤
   - **緩解**: 使用 IDE 的批次重構功能、全面測試
   - **回滾計劃**: Git 分支隔離

2. **Spring Boot 3.x 升級** 🔴
   - **風險**: 行為變更、配置不相容
   - **緩解**: 詳閱 Spring Boot 3.0 遷移指南
   - **測試重點**: JPA 實體行為、REST API 回應格式、Actuator 端點、快取行為

#### 中風險項目:
3. **Dockerfile 基礎映像更新** 🟡
   - **風險**: 容器啟動失敗
   - **緩解**: 本地 Docker 測試
   - **驗證**: 健康檢查端點

#### 低風險項目:
4. **Records 轉換** 🟢
   - **風險**: 最小 - Spring Boot 3.x 完全支援 Records
   - **緩解**: 單元測試覆蓋

5. **語法現代化** 🟢
   - Switch expressions、Pattern matching
   - **風險**: 最小 - 純語法改進

---

## 6. 建議的變更順序 (最小化風險)

### 🎯 階段 1: 基礎遷移 (必要 - 1-2 天)

#### 步驟 1.1: 環境準備 (30 分鐘)
```bash
# 1. 建立功能分支
git checkout -b feature/java-17-migration

# 2. 確認本地 Java 17 安裝
java -version  # 應顯示 17.x

# 3. 備份當前狀態
git tag pre-java17-migration
```

#### 步驟 1.2: pom.xml 更新 (30 分鐘)
1. 更新 Java 版本屬性 (17)
2. 更新 Spring Boot 版本 (3.2.6)
3. 新增 Maven Compiler Plugin 配置
4. 執行 `mvn clean compile` 驗證依賴項解析

**驗證點**: ✅ Maven 依賴項下載成功

#### 步驟 1.3: Jakarta EE 命名空間遷移 (1 小時)
**使用 IDE 批次重構**:
```
查找: import javax.persistence
替換: import jakarta.persistence

查找: import javax.validation
替換: import jakarta.validation
```

**受影響檔案**:
1. ✅ Transaction.java
2. ✅ AuthorizeRequest.java
3. ✅ CaptureRequest.java
4. ✅ RefundRequest.java
5. ✅ PaymentController.java

**驗證點**: ✅ `mvn clean compile` 成功

#### 步驟 1.4: Dockerfile 更新 (30 分鐘)
```dockerfile
# 更新建置階段
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 更新執行階段
FROM gcr.io/distroless/java17-debian12:nonroot
```

**驗證點**: ✅ `docker build -t payment-app:java17 .` 成功

#### 步驟 1.5: 編譯與基礎測試 (2 小時)
```bash
# 1. 完整編譯
mvn clean package

# 2. 執行單元測試
mvn test

# 3. 本地啟動測試
java -jar target/payment-app-1.0.0.jar

# 4. 健康檢查
curl http://localhost:8080/actuator/health

# 5. API 功能測試
curl -X POST http://localhost:8080/api/payments/authorize \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4263970000005262",
    "expiry": "12/25",
    "cvv": "123",
    "amount": 100.00
  }'
```

**驗證清單**:
- ✅ 應用程式啟動無錯誤
- ✅ Actuator 端點可存取
- ✅ 資料庫連線正常
- ✅ 快取功能正常
- ✅ API 端點回應正確
- ✅ JPA 實體持久化正常

---

### 🚀 階段 2: 程式碼現代化 (建議 - 1-2 天)

#### 步驟 2.1: Records 轉換 - 請求 DTOs (1 小時)
**順序**: AuthorizeRequest → CaptureRequest → RefundRequest

**每個類別的步驟**:
1. 建立新的 Record 版本
2. 執行測試確認序列化/反序列化
3. 確認驗證註解正常運作
4. 刪除舊類別

#### 步驟 2.2: Records 轉換 - TransactionResponse (1 小時)
1. 轉換為 Record
2. 保留 `fromTransaction()` 靜態方法
3. 保留 `maskCardNumber()` 私有方法
4. 測試 API 回應格式

#### 步驟 2.3: Optional 模式改進 (1 小時)
**PaymentService.java 三處修改**:
1. capture() 方法
2. refund() 方法
3. getTransaction() 方法

#### 步驟 2.4: Switch Expressions (30 分鐘)
**PaymentService.detectCardType() 方法改進**

#### 步驟 2.5: 整合測試與程式碼審查 (2 小時)
```bash
# 1. 完整測試套件
mvn clean verify

# 2. 程式碼覆蓋率檢查
mvn jacoco:report

# 3. 靜態分析
mvn spotbugs:check
```

---

## 7. 測試策略

### 7.1 測試層級

#### 單元測試 (必要)
- ✅ PaymentService 所有方法
- ✅ TransactionResponse.fromTransaction()
- ✅ 卡片類型偵測邏輯
- ✅ 卡片到期驗證邏輯
- ✅ Records 序列化/反序列化

#### 整合測試 (必要)
- ✅ REST API 端點
- ✅ JPA Repository 查詢
- ✅ 快取行為
- ✅ 交易管理

#### 端對端測試 (建議)
- ✅ 完整的授權-捕獲-退款流程
- ✅ 錯誤處理場景
- ✅ 並發交易處理

### 7.2 功能測試檢查清單

```markdown
## 功能測試
- [ ] 授權成功場景
- [ ] 授權失敗場景 (過期卡、餘額不足、隨機拒絕)
- [ ] 捕獲成功場景
- [ ] 捕獲失敗場景 (無效狀態、金額超出)
- [ ] 退款成功場景
- [ ] 退款失敗場景 (無效狀態、金額超出)
- [ ] 交易查詢
- [ ] 交易歷史列表

## 非功能測試
- [ ] 效能 (回應時間 < 500ms)
- [ ] 並發處理 (100 並發請求)
- [ ] 快取效能
- [ ] 記憶體使用 (< 512MB)
- [ ] 容器啟動時間 (< 30 秒)

## 相容性測試
- [ ] Kubernetes 部署
- [ ] Prometheus 指標收集
- [ ] 健康檢查端點
- [ ] 日誌格式
```

---

## 8. 部署與回滾計劃

### 8.1 部署策略

#### 藍綠部署 (建議)
```bash
# 1. 部署 Green (Java 17)
kubectl apply -f k8s/deployment-green.yaml

# 2. 驗證 Green 健康
kubectl get pods -l version=green

# 3. 切換流量
kubectl patch service payment-app -p '{"spec":{"selector":{"version":"green"}}}'

# 4. 監控 5-10 分鐘

# 5. 如果成功，刪除 Blue
kubectl delete -f k8s/deployment-blue.yaml
```

### 8.2 回滾計劃

#### 快速回滾 (< 5 分鐘)
```bash
# 切換回 Blue 部署
kubectl patch service payment-app -p '{"spec":{"selector":{"version":"blue"}}}'
```

### 8.3 監控指標

**關鍵指標**:
- ✅ 錯誤率 (< 0.1%)
- ✅ 回應時間 P95 (< 500ms)
- ✅ 記憶體使用 (< 512MB)
- ✅ CPU 使用 (< 50%)
- ✅ JVM GC 暫停時間 (< 100ms)

---

## 9. 檢查清單與驗收標準

### 9.1 遷移前檢查清單

```markdown
## 環境準備
- [ ] Java 17 已安裝並配置
- [ ] Maven 3.8.6+ 已安裝
- [ ] Docker 支援 Java 17 映像
- [ ] IDE 支援 Java 17 語法
- [ ] CI/CD 管道已更新

## 程式碼準備
- [ ] 所有變更已提交
- [ ] 建立功能分支
- [ ] 建立備份標籤
- [ ] 通知團隊成員

## 測試準備
- [ ] 測試環境可用
- [ ] 測試資料已準備
- [ ] 監控儀表板已設定
- [ ] 回滾程序已文件化
```

### 9.2 遷移後驗收標準

```markdown
## 功能驗收
- [ ] 所有 API 端點正常運作
- [ ] 資料庫操作正確
- [ ] 快取功能正常
- [ ] 日誌輸出正確
- [ ] Actuator 端點可存取

## 效能驗收
- [ ] 回應時間符合 SLA
- [ ] 記憶體使用在預期範圍
- [ ] CPU 使用正常
- [ ] GC 行為正常
- [ ] 無記憶體洩漏

## 程式碼品質
- [ ] 所有測試通過
- [ ] 程式碼覆蓋率 ≥ 80%
- [ ] 無新的靜態分析警告
- [ ] 程式碼審查已完成
- [ ] 文件已更新
```

---

## 10. 成功標準與 KPI

### 10.1 技術 KPI

| 指標 | 目標 | 測量方式 |
|------|------|---------|
| 編譯成功率 | 100% | Maven build |
| 測試通過率 | 100% | JUnit reports |
| 程式碼覆蓋率 | ≥ 80% | JaCoCo |
| API 回應時間 | ≤ 500ms P95 | Prometheus |
| 錯誤率 | < 0.1% | Application logs |
| 記憶體使用 | < 512MB | JVM metrics |

### 10.2 程式碼品質改進

| 指標 | 當前 | 目標 | 改進 |
|------|------|------|------|
| 程式碼行數 | ~1,200 | ~900 | -25% |
| 樣板程式碼 | ~400 行 | ~100 行 | -75% |
| 圈複雜度 | 平均 5 | 平均 3 | -40% |

---

## 11. 時程規劃

### 11.1 建議時程 (保守估計)

```
週 1: 準備與階段 1
├─ 第 1-2 天: 環境設定與 pom.xml 更新
├─ 第 3 天: Jakarta EE 遷移
├─ 第 4 天: 編譯與基礎測試
└─ 第 5 天: 容器化測試與文件

週 2: 階段 2 與驗證
├─ 第 1-2 天: Records 轉換
├─ 第 3 天: Optional 與 Switch 改進
├─ 第 4 天: 整合測試
└─ 第 5 天: 效能測試與程式碼審查

週 3: 部署與監控
├─ 第 1 天: 測試環境部署
├─ 第 2-3 天: 金絲雀部署到生產環境
├─ 第 4 天: 監控與調優
└─ 第 5 天: 文件更新與知識分享
```

### 11.2 快速路徑 (僅階段 1)

```
第 1 天: pom.xml + Jakarta EE 遷移
第 2 天: 測試與修復
第 3 天: 容器化與部署
```

---

## 12. 總結與建議

### 12.1 關鍵要點

1. **強制性變更** (階段 1):
   - Jakarta EE 命名空間遷移是必要的
   - Spring Boot 3.x 升級需要仔細測試
   - 預估 4 小時完成基礎遷移

2. **價值提升** (階段 2):
   - Records 可減少 70% 的樣板程式碼
   - 現代化語法提升可讀性和維護性
   - 預估 5.5 小時完成程式碼現代化

3. **風險管理**:
   - 使用 Git 分支隔離變更
   - 藍綠部署確保零停機時間
   - 完整的測試覆蓋降低風險

### 12.2 建議行動

**立即行動**:
1. ✅ 設定 Java 17 開發環境
2. ✅ 建立功能分支
3. ✅ 開始階段 1 遷移

**短期目標** (1-2 週):
1. ✅ 完成階段 1 (強制性變更)
2. ✅ 部署到測試環境
3. ✅ 驗證功能和效能

**中期目標** (2-4 週):
1. ✅ 完成階段 2 (程式碼現代化)
2. ✅ 部署到生產環境
3. ✅ 監控和優化

### 12.3 預期效益

**技術效益**:
- 🚀 使用最新的 LTS Java 版本
- 🔒 更好的安全性和效能
- 📦 更小的容器映像
- 🛠️ 更好的開發者體驗

**業務效益**:
- ⏱️ 更快的開發速度 (減少樣板程式碼)
- 🐛 更少的 bug (型別安全、不可變性)
- 💰 更低的維護成本
- 📈 更好的可擴展性

---

**文件版本**: 1.0  
**最後更新**: 2026-06-10  
**作者**: Bob (AI Software Engineer)