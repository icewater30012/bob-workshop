#!/bin/bash

# 車站管理系統 API 測試腳本
# 測試所有 REST API 端點

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "🚇 車站管理系統 API 測試"
echo "=========================================="
echo ""

# 測試計數器
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 測試函數
test_api() {
    local test_name=$1
    local method=$2
    local endpoint=$3
    local data=$4
    local expected_status=$5
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    echo -n "測試 $TOTAL_TESTS: $test_name ... "
    
    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        if [ ! -z "$body" ] && [ "$body" != "[]" ]; then
            echo "   回應: $(echo $body | head -c 100)..."
        fi
    else
        echo -e "${RED}✗ FAIL${NC} (預期: $expected_status, 實際: $http_code)"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        echo "   回應: $body"
    fi
    echo ""
}

echo "=========================================="
echo "📍 車站 API 測試"
echo "=========================================="
echo ""

# 1. 取得所有車站
test_api "取得所有車站" "GET" "/stations" "" "200"

# 2. 取得單一車站
test_api "取得單一車站 (ID=1)" "GET" "/stations/1" "" "200"

# 3. 取得不存在的車站
test_api "取得不存在的車站 (ID=999)" "GET" "/stations/999" "" "404"

# 4. 新增車站 (使用隨機數確保唯一性,限制在10字元內)
RANDOM_NUM=$((RANDOM % 10000))
NEW_STATION="{
  \"code\": \"T${RANDOM_NUM}\",
  \"name\": \"測試車站${RANDOM_NUM}\",
  \"line\": \"紅線\"
}"
test_api "新增新車站" "POST" "/stations" "$NEW_STATION" "201"

# 5. 新增重複代碼的車站 (應該失敗)
DUPLICATE_STATION='{
  "code": "R11",
  "name": "重複車站",
  "line": "紅線"
}'
test_api "新增重複代碼車站 (應失敗)" "POST" "/stations" "$DUPLICATE_STATION" "400"

# 6. 新增無效資料的車站
INVALID_STATION='{
  "code": "",
  "name": "",
  "line": "紅線"
}'
test_api "新增無效資料車站 (應失敗)" "POST" "/stations" "$INVALID_STATION" "400"

echo "=========================================="
echo "💬 回饋 API 測試"
echo "=========================================="
echo ""

# 7. 取得所有回饋
test_api "取得所有回饋" "GET" "/feedbacks" "" "200"

# 8. 新增回饋
NEW_FEEDBACK='{
  "station": { "id": 1 },
  "rating": 5,
  "comment": "測試回饋 - 服務很好!",
  "passengerName": "測試乘客A"
}'
test_api "新增新回饋" "POST" "/feedbacks" "$NEW_FEEDBACK" "201"

# 9. 新增匿名回饋
ANONYMOUS_FEEDBACK='{
  "station": { "id": 2 },
  "rating": 4,
  "comment": "測試回饋 - 環境乾淨",
  "passengerName": null
}'
test_api "新增匿名回饋" "POST" "/feedbacks" "$ANONYMOUS_FEEDBACK" "201"

# 10. 新增無效評分的回饋 (評分 > 5)
INVALID_RATING_FEEDBACK='{
  "station": { "id": 1 },
  "rating": 6,
  "comment": "測試回饋",
  "passengerName": "測試乘客B"
}'
test_api "新增無效評分回饋 (應失敗)" "POST" "/feedbacks" "$INVALID_RATING_FEEDBACK" "400"

# 11. 新增無車站的回饋
NO_STATION_FEEDBACK='{
  "rating": 5,
  "comment": "測試回饋",
  "passengerName": "測試乘客C"
}'
test_api "新增無車站回饋 (應失敗)" "POST" "/feedbacks" "$NO_STATION_FEEDBACK" "400"

# 12. 取得特定車站的回饋
test_api "取得車站1的回饋" "GET" "/feedbacks/station/1" "" "200"

# 13. 取得不存在車站的回饋
test_api "取得不存在車站的回饋" "GET" "/feedbacks/station/999" "" "404"

# 14. 取得回饋統計
test_api "取得回饋統計" "GET" "/feedbacks/statistics" "" "200"

# 15. 取得特定車站的統計資料 (包含平均評分)
test_api "取得車站1的統計資料" "GET" "/feedbacks/statistics/station/1" "" "200"

# 16. 取得回饋 (依評分篩選)
test_api "取得5星評分的回饋" "GET" "/feedbacks/rating/5" "" "200"

# 17. 取得單一回饋
test_api "取得單一回饋 (ID=1)" "GET" "/feedbacks/1" "" "200"

# 18. 取得不存在的回饋
test_api "取得不存在的回饋 (ID=999)" "GET" "/feedbacks/999" "" "404"

echo "=========================================="
echo "📊 測試結果摘要"
echo "=========================================="
echo ""
echo "總測試數: $TOTAL_TESTS"
echo -e "${GREEN}通過: $PASSED_TESTS${NC}"
echo -e "${RED}失敗: $FAILED_TESTS${NC}"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 所有測試通過!${NC}"
    exit 0
else
    echo -e "${RED}❌ 有測試失敗,請檢查!${NC}"
    exit 1
fi

# Made with Bob
