# Roo Cline Slash Commands

本目錄包含車站管理系統的自訂 Slash Commands。

## 可用命令

### /deploy-check

檢查車站管理系統部署前的各項條件。

#### 用法

```
/deploy-check [environment] [verbose]
```

#### 參數

- `environment` (選填): 目標部署環境
  - 選項: `development`, `staging`, `production`
  - 預設: `production`

- `verbose` (選填): 顯示詳細檢查資訊
  - 類型: boolean
  - 預設: `false`

#### 範例

```bash
# 檢查 production 環境 (預設)
/deploy-check

# 檢查 staging 環境
/deploy-check staging

# 檢查 production 環境並顯示詳細資訊
/deploy-check production true

# 直接執行腳本
cd /Users/voegelin/Documents/exploitation/bob-workshop/topics/station-system
./.roo/commands/deploy-check.sh production false
```

#### 檢查項目

**環境檢查**
- ✅ Java 17 或以上
- ✅ Maven 安裝
- ✅ Git 安裝
- ⚠️ Docker 安裝 (非必要)

**專案結構檢查**
- ✅ pom.xml 存在
- ✅ application.properties 存在
- ✅ 主程式類別存在
- ✅ 實體類別存在 (Station, Feedback)
- ✅ 控制器存在 (StationController, FeedbackController)
- ✅ 前端檔案存在 (HTML, CSS, JS)

**編譯檢查**
- ✅ Maven 編譯成功
- ⚠️ Maven 測試通過 (production 環境)

**安全性檢查**
- ⚠️ 無硬編碼密碼
- ⚠️ 無硬編碼 API Key
- ⚠️ CORS 設定檢查 (production 環境)

**程式碼品質檢查**
- ⚠️ TODO 註解數量
- ⚠️ System.out.println 使用

**API 端點檢查** (如果應用程式運行中)
- ✅ 車站 API 可訪問
- ✅ 回饋 API 可訪問
- ✅ 主頁面可訪問
- ✅ 回饋頁面可訪問

#### 退出碼

- `0`: 所有檢查通過或僅有警告
- `1`: 有關鍵項目失敗

#### 輸出範例

```
========================================
🚇 車站管理系統部署前檢查
========================================

目標環境: production
詳細模式: false

========================================
📦 環境檢查
========================================

[1] 檢查 Java 17 或以上 ... ✓ PASS
[2] 檢查 Maven 安裝 ... ✓ PASS
[3] 檢查 Git 安裝 ... ✓ PASS
[4] 檢查 Docker 安裝 ... ⚠ WARNING

...

========================================
📊 檢查結果摘要
========================================

總檢查項目: 25
通過: 22
警告: 3
失敗: 0

🎉 所有檢查通過! 系統已準備好部署到 production 環境
```

## 建立新的 Slash Command

1. 在 `.roo/commands/` 目錄下建立 JSON 設定檔:

```json
{
  "name": "command-name",
  "description": "命令描述",
  "version": "1.0.0",
  "author": "作者名稱",
  "parameters": [
    {
      "name": "param1",
      "description": "參數描述",
      "type": "string",
      "required": false,
      "default": "預設值"
    }
  ],
  "script": "command-script.sh"
}
```

2. 建立對應的執行腳本 (與 JSON 中的 `script` 欄位對應)

3. 賦予執行權限:
```bash
chmod +x .roo/commands/command-script.sh
```

4. 在 Roo Cline 中使用:
```
/command-name [parameters]
```

## 注意事項

- 所有腳本應該在專案根目錄執行
- 使用 `set -e` 確保錯誤時停止執行
- 提供清晰的輸出訊息和顏色標示
- 適當處理退出碼
- 支援詳細模式 (verbose) 以便除錯

## 相關文件

- [API 測試腳本](../../API_TEST.sh)
- [測試結果](../../TEST_RESULTS.md)
- [專案 README](../../README.md)