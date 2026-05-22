# 交通卡使用分析系統 - IBM Bob Workshop Sample

> **客戶**：KRTC  
> **用途**：IBM Bob Workshop 實戰演練  
> **難度**：⭐⭐⭐⭐

## 📋 專案簡介

這是一個交通卡使用分析系統的 Java Spring Boot Sample Application，依照 `journey-analysis.html` 挑戰需求建立。

目前已提供：
- Journey 基礎資料模型與 CRUD 功能
- 基本統計 API（總旅程數、總收入）
- 測試資料（12 筆旅程記錄）
- Dashboard 前端頁面

**待實作功能**（學員練習）：
- 📊 **熱門路線分析**：統計起迄站組合 Top 10
- ⏰ **尖峰時段分析**：24 小時進站分布統計

## 🚀 啟動方式

```bash
cd topics/journey-analysis
mvn spring-boot:run
```

啟動後可存取：
- Web UI: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:krtc_db`
  - Username: `sa`
  - Password: 留空
- Swagger UI: http://localhost:8080/swagger-ui.html

## 📦 API

### 已提供的基本 API：
| 方法 | 端點 | 說明 | 狀態 |
|------|------|------|------|
| GET | `/api/journeys` | 查詢所有旅程 | ✅ 已實作 |
| GET | `/api/journeys/statistics` | Dashboard 統計 | ✅ 已實作 |

### 待實作的進階 API：
| 方法 | 端點 | 說明 | 狀態 |
|------|------|------|------|
| GET | `/api/journeys/popular-routes` | 熱門路線 Top 10 | ⏳ 待實作 |
| GET | `/api/journeys/peak-hours` | 24 小時進站分布 | ⏳ 待實作 |

## 🧱 專案結構

```text
topics/journey-analysis/
├── src/main/java/com/krtc/
│   ├── JourneyAnalysisApplication.java
│   ├── controller/          # 待實作
│   ├── dto/                 # 待實作
│   ├── model/
│   │   ├── Card.java
│   │   ├── Station.java
│   │   ├── Transaction.java
│   │   └── Journey.java     # 待實作
│   ├── repository/
│   │   ├── CardRepository.java
│   │   ├── StationRepository.java
│   │   ├── TransactionRepository.java
│   │   └── JourneyRepository.java  # 待實作
│   └── service/             # 待實作
├── src/main/resources/
│   ├── application.yml
│   ├── data.sql
│   └── static/
│       ├── index.html
│       ├── styles.css
│       └── app.js
└── journey-analysis.html
```

## 📊 測試資料

已預載（供學員測試使用）：
- 5 張交通卡
- 8 個車站
- 8 筆進出站交易
- 12 筆旅程資料（需學員建立 Journey Entity 後才能使用）

## 🛠 技術棧

- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- SpringDoc OpenAPI

## 🎯 學習目標

完成本挑戰後，學員將能夠：
- 使用 Bob 從現有程式碼快速新增功能
- 實作複雜的資料聚合與統計分析
- 設計 RESTful API 端點
- 撰寫專案 Rules 與 AGENTS.md
- 建立自訂 Slash Command

## 🎓 實作重點

學員需要實作兩個進階分析功能：

### 1. 熱門路線分析（RouteAnalysisDto + API）
- 在 `JourneyRepository` 新增聚合查詢方法
- 在 `JourneyService` 實作 `getPopularRoutes()` 方法
- 在 `JourneyController` 新增 `GET /api/journeys/popular-routes` 端點
- 建立 `RouteAnalysisDto` 記錄類別

### 2. 尖峰時段分析（PeakHourDto + API）
- 在 `JourneyRepository` 新增時段查詢方法
- 在 `JourneyService` 實作 `getPeakHours()` 方法
- 在 `JourneyController` 新增 `GET /api/journeys/peak-hours` 端點
- 建立 `PeakHourDto` 記錄類別

### 3. Bob 進階功能
- 自訂 `/security-scan` Slash Command
- 編碼規範 Rules
- AGENTS.md 指引文件