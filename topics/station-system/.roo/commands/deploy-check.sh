#!/bin/bash

# 車站管理系統部署前檢查腳本
# 用於 Roo Cline Slash Command: /deploy-check

set -e

# 顏色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 參數
ENVIRONMENT=${1:-production}
VERBOSE=${2:-false}

# 檢查計數器
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0
WARNING_CHECKS=0

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🚇 車站管理系統部署前檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "目標環境: ${YELLOW}$ENVIRONMENT${NC}"
echo -e "詳細模式: ${YELLOW}$VERBOSE${NC}"
echo ""

# 檢查函數
check_item() {
    local check_name=$1
    local check_command=$2
    local is_critical=${3:-true}
    
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    echo -n "[$TOTAL_CHECKS] 檢查 $check_name ... "
    
    if eval "$check_command" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ PASS${NC}"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
        return 0
    else
        if [ "$is_critical" = true ]; then
            echo -e "${RED}✗ FAIL${NC}"
            FAILED_CHECKS=$((FAILED_CHECKS + 1))
        else
            echo -e "${YELLOW}⚠ WARNING${NC}"
            WARNING_CHECKS=$((WARNING_CHECKS + 1))
        fi
        
        if [ "$VERBOSE" = "true" ]; then
            echo "   命令: $check_command"
            eval "$check_command" 2>&1 | sed 's/^/   /'
        fi
        return 1
    fi
}

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}📦 環境檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Java 版本檢查
check_item "Java 17 或以上" "java -version 2>&1 | grep -E 'version \"(17|[2-9][0-9])'"

# Maven 檢查
check_item "Maven 安裝" "mvn -version"

# Git 檢查
check_item "Git 安裝" "git --version"

# Docker 檢查 (非必要)
check_item "Docker 安裝" "docker --version" false

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🔧 專案結構檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 檢查必要檔案
check_item "pom.xml 存在" "test -f pom.xml"
check_item "application.properties 存在" "test -f src/main/resources/application.properties"
check_item "主程式類別存在" "test -f src/main/java/com/metro/StationApplication.java"

# 檢查實體類別
check_item "Station 實體存在" "test -f src/main/java/com/metro/model/Station.java"
check_item "Feedback 實體存在" "test -f src/main/java/com/metro/model/Feedback.java"

# 檢查控制器
check_item "StationController 存在" "test -f src/main/java/com/metro/controller/StationController.java"
check_item "FeedbackController 存在" "test -f src/main/java/com/metro/controller/FeedbackController.java"

# 檢查前端檔案
check_item "index.html 存在" "test -f src/main/resources/static/index.html"
check_item "feedback.html 存在" "test -f src/main/resources/static/feedback.html"
check_item "CSS 檔案存在" "test -f src/main/resources/static/css/style.css"
check_item "app.js 存在" "test -f src/main/resources/static/js/app.js"
check_item "feedback.js 存在" "test -f src/main/resources/static/js/feedback.js"

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🏗️ 編譯檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Maven 編譯檢查
check_item "Maven 編譯成功" "mvn clean compile -q"

# Maven 測試 (非必要)
if [ "$ENVIRONMENT" = "production" ]; then
    check_item "Maven 測試通過" "mvn test -q" false
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🔒 安全性檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 檢查敏感資訊
check_item "無硬編碼密碼" "! grep -r 'password.*=.*[^{]' src/main/resources/ 2>/dev/null" false
check_item "無硬編碼 API Key" "! grep -r 'api[_-]key.*=.*[^{]' src/main/resources/ 2>/dev/null" false

# 檢查 CORS 設定
if [ "$ENVIRONMENT" = "production" ]; then
    check_item "CORS 設定檢查" "! grep -r '@CrossOrigin(origins = \"\\*\")' src/main/java/ 2>/dev/null" false
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}📝 程式碼品質檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 檢查 TODO 註解
TODO_COUNT=$(grep -r "TODO" src/main/java/ 2>/dev/null | wc -l | tr -d ' ')
if [ "$TODO_COUNT" -gt 0 ]; then
    echo -e "[$((TOTAL_CHECKS + 1))] 檢查 TODO 註解 ... ${YELLOW}⚠ WARNING${NC} (發現 $TODO_COUNT 個)"
    WARNING_CHECKS=$((WARNING_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
else
    echo -e "[$((TOTAL_CHECKS + 1))] 檢查 TODO 註解 ... ${GREEN}✓ PASS${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
fi

# 檢查 System.out.println
SYSOUT_COUNT=$(grep -r "System.out.println" src/main/java/ 2>/dev/null | wc -l | tr -d ' ')
if [ "$SYSOUT_COUNT" -gt 0 ]; then
    echo -e "[$((TOTAL_CHECKS + 1))] 檢查 System.out.println ... ${YELLOW}⚠ WARNING${NC} (發現 $SYSOUT_COUNT 個)"
    WARNING_CHECKS=$((WARNING_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
else
    echo -e "[$((TOTAL_CHECKS + 1))] 檢查 System.out.println ... ${GREEN}✓ PASS${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🌐 API 端點檢查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 檢查應用程式是否運行
if lsof -ti:8080 > /dev/null 2>&1; then
    echo -e "[$((TOTAL_CHECKS + 1))] 應用程式運行中 ... ${GREEN}✓ PASS${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    
    # 測試 API 端點
    check_item "車站 API 可訪問" "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/stations | grep -q 200"
    check_item "回饋 API 可訪問" "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/feedbacks | grep -q 200"
    check_item "主頁面可訪問" "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/ | grep -q 200"
    check_item "回饋頁面可訪問" "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/feedback.html | grep -q 200"
else
    echo -e "[$((TOTAL_CHECKS + 1))] 應用程式運行中 ... ${YELLOW}⚠ WARNING${NC} (應用程式未運行)"
    WARNING_CHECKS=$((WARNING_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    echo "   提示: 執行 './mvnw spring-boot:run' 啟動應用程式"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}📊 檢查結果摘要${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "總檢查項目: $TOTAL_CHECKS"
echo -e "${GREEN}通過: $PASSED_CHECKS${NC}"
echo -e "${YELLOW}警告: $WARNING_CHECKS${NC}"
echo -e "${RED}失敗: $FAILED_CHECKS${NC}"
echo ""

# 計算通過率
PASS_RATE=$((PASSED_CHECKS * 100 / TOTAL_CHECKS))

if [ $FAILED_CHECKS -eq 0 ]; then
    if [ $WARNING_CHECKS -eq 0 ]; then
        echo -e "${GREEN}🎉 所有檢查通過! 系統已準備好部署到 $ENVIRONMENT 環境${NC}"
        exit 0
    else
        echo -e "${YELLOW}⚠️  有 $WARNING_CHECKS 個警告項目,建議修正後再部署${NC}"
        echo -e "${YELLOW}通過率: $PASS_RATE%${NC}"
        exit 0
    fi
else
    echo -e "${RED}❌ 有 $FAILED_CHECKS 個關鍵項目失敗,請修正後再部署!${NC}"
    echo -e "${RED}通過率: $PASS_RATE%${NC}"
    exit 1
fi

# Made with Bob
