# Deploy Check 使用指南

## 📋 什麼是 Deploy Check?

Deploy Check 是一個部署前檢查工具,會自動檢查系統的各項條件,確保應用程式可以安全部署。

## 🚀 如何使用

### 方法 1: 直接執行腳本

```bash
# 進入專案目錄
cd /Users/voegelin/Documents/exploitation/bob-workshop/topics/station-system

# 執行部署檢查 (production 環境)
./.roo/commands/deploy-check.sh production false

# 執行部署檢查 (staging 環境)
./.roo/commands/deploy-check.sh staging false

# 執行部署檢查 (詳細模式)
./.roo/commands/deploy-check.sh production true
```

### 方法 2: 使用 Roo Cline Slash Command (未來功能)

```
/deploy-check production
/deploy-check staging true
```

## 📊 檢查項目

### 1. 環境檢查 (4 項)
- ✅ Java 17 或以上
- ✅ Maven 安裝
- ✅ Git 安裝
- ⚠️ Docker 安裝 (非必要)

### 2. 專案結構檢查 (9 項)
- ✅ pom.xml 存在
- ✅ application.properties 存在
- ✅ 主程式類別存在
- ✅ Station 實體存在
- ✅ Feedback 實體存在
- ✅ StationController 存在
- ✅ FeedbackController 存在
- ✅ index.html 存在
- ✅ feedback.html 存在
- ✅ CSS 檔案存在
- ✅ JavaScript 檔案存在

### 3. 編譯檢查 (2 項)
- ✅ Maven 編譯成功
- ⚠️ Maven 測試通過 (production 環境)

### 4. 安全性檢查 (3 項)
- ⚠️ 無硬編碼密碼
- ⚠️ 無硬編碼 API Key
- ⚠️ CORS 設定檢查 (production 環境)

### 5. 程式碼品質檢查 (2 項)
- ⚠️ TODO 註解數量
- ⚠️ System.out.println 使用

### 6. API 端點檢查 (5 項,需應用程式運行)
- ✅ 應用程式運行中
- ✅ 車站 API 可訪問
- ✅ 回饋 API 可訪問
- ✅ 主頁面可訪問
- ✅ 回饋頁面可訪問

## 📈 輸出範例

```bash
==========================================
🚇 車站管理系統部署前檢查
==========================================

目標環境: production
詳細模式: false

==========================================
📦 環境檢查
==========================================

[1] 檢查 Java 17 或以上 ... ✓ PASS
[2] 檢查 Maven 安裝 ... ✓ PASS
[3] 檢查 Git 安裝 ... ✓ PASS
[4] 檢查 Docker 安裝 ... ⚠ WARNING

==========================================
🔧 專案結構檢查
==========================================

[5] 檢查 pom.xml 存在 ... ✓ PASS
[6] 檢查 application.properties 存在 ... ✓ PASS
...

==========================================
📊 檢查結果摘要
==========================================

總檢查項目: 25
通過: 22
警告: 3
失敗: 0

🎉 所有檢查通過! 系統已準備好部署到 production 環境
```

## 🎯 參數說明

### environment (環境)
- `development` - 開發環境
- `staging` - 測試環境
- `production` - 生產環境 (預設)

不同環境會有不同的檢查標準:
- **production**: 最嚴格,會檢查 CORS、執行測試
- **staging**: 中等嚴格
- **development**: 最寬鬆

### verbose (詳細模式)
- `true` - 顯示詳細的錯誤訊息和命令輸出
- `false` - 只顯示檢查結果 (預設)

## 🔍 解讀結果

### ✓ PASS (通過)
- 綠色標記
- 該項目檢查通過
- 無需採取行動

### ⚠ WARNING (警告)
- 黃色標記
- 該項目有潛在問題
- 建議修正,但不影響部署

### ✗ FAIL (失敗)
- 紅色標記
- 該項目檢查失敗
- **必須修正**才能部署

## 📝 常見問題

### Q1: 如何修正 "Docker 安裝" 警告?
**A**: Docker 是非必要項目,如果不需要容器化部署可以忽略。

### Q2: 如何修正 "TODO 註解" 警告?
**A**: 檢查程式碼中的 TODO 註解,完成或移除它們。

### Q3: 如何修正 "System.out.println" 警告?
**A**: 將 `System.out.println` 改為使用 Logger:
```java
// 不好
System.out.println("Debug message");

// 好
logger.info("Debug message");
```

### Q4: 如何修正 "CORS 設定" 警告?
**A**: 在 production 環境,不應該使用 `@CrossOrigin(origins = "*")`:
```java
// 不好 (允許所有來源)
@CrossOrigin(origins = "*")

// 好 (指定允許的來源)
@CrossOrigin(origins = "https://yourdomain.com")
```

### Q5: 應用程式未運行怎麼辦?
**A**: 啟動應用程式:
```bash
./mvnw spring-boot:run
```

## 🛠️ 進階使用

### 整合到 CI/CD

```bash
#!/bin/bash
# 在 CI/CD pipeline 中使用

cd /path/to/project

# 執行檢查
./.roo/commands/deploy-check.sh production false

# 檢查退出碼
if [ $? -eq 0 ]; then
    echo "✓ 部署檢查通過,繼續部署"
    # 執行部署命令
else
    echo "✗ 部署檢查失敗,停止部署"
    exit 1
fi
```

### 自動化測試流程

```bash
# 1. 執行 API 測試
./API_TEST.sh

# 2. 執行部署檢查
./.roo/commands/deploy-check.sh production false

# 3. 如果都通過,進行部署
if [ $? -eq 0 ]; then
    echo "準備部署..."
fi
```

## 📚 相關文件

- [API 測試指南](./API_TEST.sh)
- [測試結果報告](./TEST_RESULTS.md)
- [Slash Commands 說明](./.roo/commands/README.md)
- [Custom Modes 說明](./.roo/modes/README.md)

## 💡 最佳實踐

1. **每次部署前執行** - 確保系統狀態良好
2. **修正所有 FAIL 項目** - 不要忽略失敗的檢查
3. **關注 WARNING 項目** - 雖然不影響部署,但應該修正
4. **使用詳細模式除錯** - 當檢查失敗時,使用 `verbose=true` 查看詳細資訊
5. **整合到 CI/CD** - 自動化部署檢查流程

## 🎓 快速開始

```bash
# 1. 確保應用程式正在運行
./mvnw spring-boot:run &

# 2. 等待應用程式啟動 (約 10 秒)
sleep 10

# 3. 執行部署檢查
./.roo/commands/deploy-check.sh production false

# 4. 查看結果並根據建議修正問題
```

---

**提示**: 如果您是第一次使用,建議先在 `development` 環境測試:
```bash
./.roo/commands/deploy-check.sh development false