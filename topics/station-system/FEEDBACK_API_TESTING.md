# 🧪 Feedback API 測試指南

本文件提供完整的 Feedback API 測試範例,使用 `curl` 命令進行測試。

## 📋 前置準備

1. 確保應用程式已啟動:
```bash
./mvnw spring-boot:run
```

2. 應用程式運行在: `http://localhost:8080`

---

## 🔍 API 端點列表

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | `/api/feedbacks` | 取得所有回饋 |
| GET | `/api/feedbacks/{id}` | 取得單一回饋 |
| POST | `/api/feedbacks` | 建立新回饋 |
| PUT | `/api/feedbacks/{id}` | 更新回饋 |
| DELETE | `/api/feedbacks/{id}` | 刪除回饋 |
| GET | `/api/feedbacks/station/{stationId}` | 依車站查詢回饋 |
| GET | `/api/feedbacks/rating/{rating}` | 依評分查詢回饋 |
| GET | `/api/feedbacks/statistics` | 取得整體統計 |
| GET | `/api/feedbacks/statistics/station/{stationId}` | 取得車站統計 |

---

## 🧪 測試步驟

### 1️⃣ 查看所有車站 (確認車站 ID)

```bash
curl http://localhost:8080/api/stations
```

**預期回應**: 返回所有車站列表,記下車站 ID (例如: 1, 2, 3...)

---

### 2️⃣ 建立第一個回饋

```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 1},
    "rating": 5,
    "comment": "車站設施非常乾淨整潔,服務人員態度親切!",
    "passengerName": "王小明"
  }'
```

**預期回應**: 
```json
{
  "id": 1,
  "station": {
    "id": 1,
    "code": "RK1",
    "name": "岡山車站",
    "line": "紅線",
    "createdAt": "..."
  },
  "rating": 5,
  "comment": "車站設施非常乾淨整潔,服務人員態度親切!",
  "passengerName": "王小明",
  "createdAt": "..."
}
```

---

### 3️⃣ 建立更多測試回饋

**回饋 2 - 評分 4**:
```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 1},
    "rating": 4,
    "comment": "整體不錯,但尖峰時段人潮較多",
    "passengerName": "李小華"
  }'
```

**回饋 3 - 評分 3**:
```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 2},
    "rating": 3,
    "comment": "普通,希望能增加更多座位",
    "passengerName": "張大明"
  }'
```

**回饋 4 - 評分 2**:
```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 2},
    "rating": 2,
    "comment": "電扶梯經常故障,造成不便"
  }'
```

**回饋 5 - 評分 5**:
```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 3},
    "rating": 5,
    "comment": "無障礙設施完善,值得讚賞!",
    "passengerName": "陳小美"
  }'
```

---

### 4️⃣ 取得所有回饋

```bash
curl http://localhost:8080/api/feedbacks
```

**預期回應**: 返回所有回饋的 JSON 陣列

---

### 5️⃣ 取得單一回饋

```bash
curl http://localhost:8080/api/feedbacks/1
```

**預期回應**: 返回 ID 為 1 的回饋詳細資料

---

### 6️⃣ 依車站查詢回饋

查詢車站 ID 為 1 的所有回饋:
```bash
curl http://localhost:8080/api/feedbacks/station/1
```

**預期回應**: 返回該車站的所有回饋

---

### 7️⃣ 依評分查詢回饋

查詢所有 5 星評分的回饋:
```bash
curl http://localhost:8080/api/feedbacks/rating/5
```

**預期回應**: 返回所有評分為 5 的回饋

---

### 8️⃣ 取得整體統計資料

```bash
curl http://localhost:8080/api/feedbacks/statistics
```

**預期回應**:
```json
{
  "totalFeedbacks": 5,
  "averageRating": 3.8,
  "ratingDistribution": {
    "1": 0,
    "2": 1,
    "3": 1,
    "4": 1,
    "5": 2
  }
}
```

---

### 9️⃣ 取得特定車站的統計資料

查詢車站 ID 為 1 的統計:
```bash
curl http://localhost:8080/api/feedbacks/statistics/station/1
```

**預期回應**:
```json
{
  "stationId": 1,
  "stationName": "岡山車站",
  "stationCode": "RK1",
  "stationLine": "紅線",
  "totalFeedbacks": 2,
  "averageRating": 4.5,
  "ratingDistribution": {
    "1": 0,
    "2": 0,
    "3": 0,
    "4": 1,
    "5": 1
  }
}
```

---

### 🔟 更新回饋

更新 ID 為 1 的回饋:
```bash
curl -X PUT http://localhost:8080/api/feedbacks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "rating": 4,
    "comment": "更新後的評論:整體很好,但還有改進空間"
  }'
```

**預期回應**: 返回更新後的回饋資料

---

### 1️⃣1️⃣ 刪除回饋

刪除 ID 為 1 的回饋:
```bash
curl -X DELETE http://localhost:8080/api/feedbacks/1
```

**預期回應**: HTTP 204 No Content (無內容回應)

驗證刪除:
```bash
curl http://localhost:8080/api/feedbacks/1
```

**預期回應**: HTTP 404 Not Found

---

## ❌ 錯誤測試案例

### 測試 1: 建立回饋時未指定車站

```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "rating": 5,
    "comment": "測試錯誤處理"
  }'
```

**預期回應**: 
```json
{
  "error": "必須指定車站"
}
```

---

### 測試 2: 評分超出範圍

```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 1},
    "rating": 6,
    "comment": "測試錯誤處理"
  }'
```

**預期回應**: 
```json
{
  "error": "評分必須在 1-5 之間"
}
```

---

### 測試 3: 回饋內容為空

```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 1},
    "rating": 5,
    "comment": ""
  }'
```

**預期回應**: 
```json
{
  "error": "回饋內容不能為空"
}
```

---

### 測試 4: 車站不存在

```bash
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "station": {"id": 9999},
    "rating": 5,
    "comment": "測試不存在的車站"
  }'
```

**預期回應**: 
```json
{
  "error": "車站不存在: ID = 9999"
}
```

---

## 📊 完整測試腳本

將以下內容儲存為 `test_feedback_api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "🧪 開始測試 Feedback API..."
echo ""

echo "1️⃣ 建立回饋 1 (評分 5)"
curl -X POST $BASE_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{"station":{"id":1},"rating":5,"comment":"車站設施非常乾淨整潔","passengerName":"王小明"}'
echo -e "\n"

echo "2️⃣ 建立回饋 2 (評分 4)"
curl -X POST $BASE_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{"station":{"id":1},"rating":4,"comment":"整體不錯","passengerName":"李小華"}'
echo -e "\n"

echo "3️⃣ 建立回饋 3 (評分 3)"
curl -X POST $BASE_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{"station":{"id":2},"rating":3,"comment":"普通","passengerName":"張大明"}'
echo -e "\n"

echo "4️⃣ 取得所有回饋"
curl $BASE_URL/api/feedbacks
echo -e "\n"

echo "5️⃣ 取得車站 1 的回饋"
curl $BASE_URL/api/feedbacks/station/1
echo -e "\n"

echo "6️⃣ 取得整體統計"
curl $BASE_URL/api/feedbacks/statistics
echo -e "\n"

echo "7️⃣ 取得車站 1 的統計"
curl $BASE_URL/api/feedbacks/statistics/station/1
echo -e "\n"

echo "✅ 測試完成!"
```

執行測試腳本:
```bash
chmod +x test_feedback_api.sh
./test_feedback_api.sh
```

---

## 🎯 測試檢查清單

- [ ] 成功建立回饋
- [ ] 取得所有回饋
- [ ] 取得單一回饋
- [ ] 依車站查詢回饋
- [ ] 依評分查詢回饋
- [ ] 取得整體統計資料
- [ ] 取得車站統計資料
- [ ] 更新回饋
- [ ] 刪除回饋
- [ ] 錯誤處理測試 (無車站、評分超範圍、空內容等)

---

## 💡 提示

1. **使用 jq 美化 JSON 輸出**:
   ```bash
   curl http://localhost:8080/api/feedbacks | jq
   ```

2. **查看 HTTP 狀態碼**:
   ```bash
   curl -i http://localhost:8080/api/feedbacks
   ```

3. **儲存回應到檔案**:
   ```bash
   curl http://localhost:8080/api/feedbacks > feedbacks.json
   ```

---

**測試愉快!** 🚀

<!-- Made with Bob -->