# Bob Workshop - 實戰演練專案

這是 Bob Workshop 的實戰演練專案集合，包含多個實際應用場景的 Sample Applications，讓學員透過實作學習如何使用 Bob 進行 AI 輔助開發。

## 🎯 Workshop 目標

本 Workshop 旨在幫助開發者：
- 🤖 **掌握 AI 輔助開發**：學習如何有效使用 Bob 提升開發效率
- 💡 **實戰經驗累積**：透過真實場景的 Sample Application 進行實作
- 📚 **最佳實踐學習**：了解 Bob Rules、AGENTS.md、SKILL.md 等進階功能
- 🔧 **工具整合應用**：學習 Slash Commands、Memory Import 等實用技巧
- 🚀 **快速開發能力**：從現有程式碼基礎上快速新增功能

## 📁 專案結構

```
bob-workshop/
├── index.html                      # Workshop 行前準備網頁
├── styles.css                      # 樣式表
├── script.js                       # JavaScript 功能
├── assets/                         # 共用資源
│   └── workshop-theme.css          # Workshop 主題樣式
├── frontend-slides/                # 📊 簡報製作 SKILL
│   ├── SKILL.md                    # 簡報製作技能說明
│   ├── html-template.md            # HTML 簡報模板
│   ├── STYLE_PRESETS.md            # 預設樣式庫
│   └── bold-template-pack/         # 40+ 專業簡報模板
├── topics/                         # 實戰主題
│   ├── station-system/             # 🚇 捷運站務管理系統（開發者）
│   ├── transaction-monitor/        # 💳 信用卡交易監控系統（開發者）
│   └── journey-analysis/           # 🗺️ 交通卡使用分析系統（開發者）
└── README.md                       # 本文件
```

## 🚇 實戰主題：捷運站務管理系統

**難度**：⭐⭐ 簡單  
**時長**：1.5 小時


### 專案簡介
建立捷運站務管理系統，提供站點資訊查詢、路線規劃等功能。

### 快速開始
```bash
cd topics/station-system
./mvnw spring-boot:run
```

詳細說明請參考：[station-system/README.md](topics/station-system/README.md)

## 💳 實戰主題：信用卡交易監控系統

**難度**：⭐⭐⭐ 中等  
**時長**：2 小時

### 專案簡介
為現有的信用卡交易系統新增即時交易監控功能，識別異常交易模式並提供風險預警。

### 學習重點
- 從現有程式碼快速新增功能
- 建立自訂 Slash Command
- 撰寫專案 Rules 確保程式碼品質
- 建立 AGENTS.md 指導 AI 助手
- 實作金融系統的安全最佳實踐

### 快速開始
```bash
cd topics/transaction-monitor
./mvnw spring-boot:run
```

詳細說明請參考：[transaction-monitor/README.md](topics/transaction-monitor/README.md)

## 🗺️ 實戰主題：交通卡使用分析系統

**難度**：⭐⭐⭐⭐ 進階
**時長**：2 小時

### 專案簡介
分析使用者旅程資料，提供個人化建議和優化方案。

### 學習重點
- 複雜資料關聯分析與處理
- 多表 JOIN 查詢優化
- 統計分析與資料視覺化
- RESTful API 設計最佳實踐
- 前後端整合開發

### 快速開始
```bash
cd topics/journey-analysis
./mvnw spring-boot:run
```

詳細說明請參考：[journey-analysis/journey-analysis.html](topics/journey-analysis/journey-analysis.html)

---

## 🚀 如何使用本專案

### 1. Fork 專案到你的 GitHub（團隊協作必要步驟）

**重要**：本 Workshop 採用 3 人協作模式，建議先 Fork 專案到你們的團隊 GitHub Repository：

1. 前往 [bob-workshop](https://github.com/icewater30012/bob-workshop)
2. 點擊右上角 **Fork** 按鈕
3. 選擇你的 GitHub 帳號或組織
4. Clone 你們 Fork 的版本：
   ```bash
   git clone git@github.com:YOUR_USERNAME/bob-workshop.git
   cd bob-workshop
   ```

**協作優勢**：
- ✅ 各成員可建立自己的分支開發
- ✅ 透過 Pull Request 進行 Code Review
- ✅ 避免直接修改原始專案
- ✅ 保留完整的開發歷程

### 2. 選擇實戰主題

#### 💻 開發者
- **初學者**：從 `station-system` 開始
- **中級開發者**：嘗試 `transaction-monitor`
- **進階挑戰**：挑戰 `journey-analysis`

### 3. 啟動 Sample Application
每個主題都包含完整的 Spring Boot 應用程式：
```bash
cd topics/[主題名稱]
./mvnw spring-boot:run
```

### 4. 使用 Bob 開始開發
在 Bob IDE 中開啟專案，參考各主題的說明文件開始實作。

## ✨ 功能特色

### 行前準備網頁
- 🎨 **官網配色**：採用 Bob 品牌色彩系統
- 📱 **完全響應式**：支援桌面、平板、手機等各種裝置
- 🔄 **雙分頁設計**：完整版和精簡版內容切換
- ♿ **無障礙支援**：符合 ARIA 標準，支援鍵盤導航

### Sample Applications
- 💻 **完整可運行**：每個主題都是完整的 Spring Boot 應用程式
- 📖 **詳細文件**：包含 README、API 文件、使用說明
- 🎯 **實戰導向**：基於真實業務場景設計
- 🔧 **漸進式學習**：從簡單到複雜，循序漸進

## 📄 授權

此專案為 Bob Workshop 使用。

## 👤 聯絡資訊

**技術支援窗口**：

- 姓名: Owen Chen
- Email: owenchen@tw.ibm.com
- 電話: 0928107182

- 姓名: Raphael Li
- Email: Raphael.Li@ibm.com
- 電話: 0925250277

## 📝 更新日誌

### v1.2.0 (2026-06-14)
- 🔧 修正交通卡使用分析系統時長為 2 小時
- 🔗 更新 GitHub Repository 連結
- 👤 新增技術支援聯絡資訊

### v1.1.0 (2026-05-13)
- ✨ 新增信用卡交易監控系統主題
- 📚 更新 README 說明 Workshop 目標與方向
- 🔗 新增 GitHub Repository 連結
- 📖 完善使用說明文件

### v1.0.0 (2026-05-05)
- ✨ 初始版本發布
- 🎨 深色科技風格設計
- 📱 完整響應式支援
- ♿ 無障礙功能實作
- 🔄 雙分頁內容切換

---

**文件版本**: 1.2
**最後更新**: 2026-06-14

**Repository**: [git@github.com:icewater30012/bob-workshop.git](https://github.com/icewater30012/bob-workshop.git)