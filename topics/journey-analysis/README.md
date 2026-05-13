# 交通卡使用分析系統 - IBM Bob Workshop Sample

> **客戶**：KRTC  
> **用途**：IBM Bob Workshop 實戰演練  
> **難度**：⭐⭐⭐⭐

## 📋 專案簡介

這是一個交通卡使用分析系統的 Java Spring Boot Sample Application，依照 `journey-analysis.html` 挑戰需求建立，並參考 `transaction-monitor` 的首頁說明呈現方式。

目前已提供：
- Journey 分析資料模型
- 熱門路線分析 API
- 尖峰時段分析 API
- Dashboard 首頁與表格查詢

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

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | `/api/journeys` | 查詢所有旅程 |
| GET | `/api/journeys/statistics` | Dashboard 統計 |
| GET | `/api/journeys/popular-routes` | 熱門路線 Top 10 |
| GET | `/api/journeys/peak-hours` | 24 小時進站分布 |

## 🧱 專案結構

```text
topics/journey-analysis/
├── src/main/java/com/krtc/
│   ├── JourneyAnalysisApplication.java
│   ├── controller/JourneyController.java
│   ├── dto/
│   ├── model/
│   ├── repository/
│   └── service/JourneyService.java
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

已預載：
- 5 張交通卡
- 8 個車站
- 8 筆進出站交易
- 12 筆旅程資料

## 🛠 技術棧

- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- SpringDoc OpenAPI

## 🎯 後續可延伸

- 補 `JourneyRepository` 聚合查詢，改由 DB 統計
- 新增單元測試
- 建立 `.bob/` 規則與文件
- 擴充 POST / 查詢條件 API